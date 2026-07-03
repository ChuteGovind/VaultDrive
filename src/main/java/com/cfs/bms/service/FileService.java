package com.cfs.bms.service;

import com.cfs.bms.dto.FileResponse;
import com.cfs.bms.dto.FileUpdateRequest;
import com.cfs.bms.entity.FileCategory;
import com.cfs.bms.entity.StoredFile;
import com.cfs.bms.entity.User;
import com.cfs.bms.exception.ResourceNotFoundException;
import com.cfs.bms.exception.UnauthorizedAccessException;
import com.cfs.bms.repository.StoredFileRepository;
import com.cfs.bms.util.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.criteria.Predicate;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FileService {

    private final StoredFileRepository fileRepository;
    private final StorageService storageService;
    private final AuthService authService;

    public FileService(StoredFileRepository fileRepository, StorageService storageService, AuthService authService) {
        this.fileRepository = fileRepository;
        this.storageService = storageService;
        this.authService = authService;
    }

    @Transactional
    public FileResponse uploadFile(MultipartFile file, String description, FileCategory customCategory) {
        User currentUser = authService.getCurrentUser();

        // Safe Filename
        String originalFileName = FileUtils.cleanFileName(file.getOriginalFilename());
        
        // Store physically
        String[] storageInfo = storageService.store(file, currentUser.getId());
        String uniqueStoredName = storageInfo[0];
        String absolutePath = storageInfo[1];

        // Determine category
        FileCategory category = customCategory;
        if (category == null) {
            category = FileUtils.determineCategory(file.getContentType(), originalFileName);
        }

        StoredFile storedFile = StoredFile.builder()
                .originalFileName(originalFileName)
                .storedFileName(uniqueStoredName)
                .description(description)
                .category(category)
                .mimeType(file.getContentType() == null ? "application/octet-stream" : file.getContentType())
                .extension(FileUtils.getExtension(originalFileName))
                .fileSize(file.getSize())
                .storagePath(absolutePath)
                .owner(currentUser)
                .build();

        StoredFile savedFile = fileRepository.save(storedFile);
        return mapToFileResponse(savedFile);
    }

    public List<FileResponse> getUserFiles() {
        User currentUser = authService.getCurrentUser();
        return fileRepository.findByOwnerOrderByUploadedAtDesc(currentUser)
                .stream()
                .map(this::mapToFileResponse)
                .collect(Collectors.toList());
    }

    public List<FileResponse> getRecentFiles() {
        User currentUser = authService.getCurrentUser();
        return fileRepository.findTop5ByOwnerOrderByUploadedAtDesc(currentUser)
                .stream()
                .map(this::mapToFileResponse)
                .collect(Collectors.toList());
    }

    public List<FileResponse> getFilesByCategory(FileCategory category) {
        User currentUser = authService.getCurrentUser();
        return fileRepository.findByOwnerAndCategoryOrderByUploadedAtDesc(currentUser, category)
                .stream()
                .map(this::mapToFileResponse)
                .collect(Collectors.toList());
    }

    public FileResponse getFileDetails(Long id) {
        StoredFile storedFile = getValidatedFile(id);
        return mapToFileResponse(storedFile);
    }

    @Transactional
    public FileResponse updateFile(Long id, FileUpdateRequest request) {
        StoredFile storedFile = getValidatedFile(id);
        
        if (request.getOriginalFileName() != null && !request.getOriginalFileName().trim().isEmpty()) {
            String newCleanName = FileUtils.cleanFileName(request.getOriginalFileName().trim());
            // Preserve the original extension if the user omitted it or it changed
            String oldExt = storedFile.getExtension();
            String newExt = FileUtils.getExtension(newCleanName);
            if (newExt.isEmpty() && !oldExt.isEmpty()) {
                newCleanName = newCleanName + "." + oldExt;
            }
            storedFile.setOriginalFileName(newCleanName);
            storedFile.setExtension(FileUtils.getExtension(newCleanName));
        }
        
        storedFile.setDescription(request.getDescription());
        
        if (request.getCategory() != null) {
            storedFile.setCategory(request.getCategory());
        }

        StoredFile updated = fileRepository.save(storedFile);
        return mapToFileResponse(updated);
    }

    @Transactional
    public void deleteFile(Long id) {
        StoredFile storedFile = getValidatedFile(id);
        User currentUser = authService.getCurrentUser();

        // 1. Delete physical file
        storageService.delete(storedFile.getStoredFileName(), currentUser.getId());

        // 2. Delete DB Entry
        fileRepository.delete(storedFile);
    }

    public Resource loadFileAsResource(Long id) {
        StoredFile storedFile = getValidatedFile(id);
        User currentUser = authService.getCurrentUser();
        
        try {
            Path filePath = storageService.load(storedFile.getStoredFileName(), currentUser.getId());
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not readable: " + storedFile.getOriginalFileName());
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File path invalid: " + storedFile.getOriginalFileName(), ex);
        }
    }

    /**
     * Specification-based filtering only within current user's files.
     */
    public List<FileResponse> searchFiles(String name, FileCategory category, LocalDate startDate, LocalDate endDate) {
        User currentUser = authService.getCurrentUser();
        
        Specification<StoredFile> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Constrain search to only owned files
            predicates.add(criteriaBuilder.equal(root.get("owner"), currentUser));
            
            if (name != null && !name.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("originalFileName")), 
                        "%" + name.trim().toLowerCase() + "%"
                ));
            }
            
            if (category != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }
            
            if (startDate != null) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("uploadedAt"), startDateTime));
            }
            
            if (endDate != null) {
                LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("uploadedAt"), endDateTime));
            }
            
            query.orderBy(criteriaBuilder.desc(root.get("uploadedAt")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return fileRepository.findAll(spec)
                .stream()
                .map(this::mapToFileResponse)
                .collect(Collectors.toList());
    }

    /**
     * Computes storage stats: total size, file counts and size grouping by category.
     */
    public Map<String, Object> getStorageStats() {
        User currentUser = authService.getCurrentUser();
        
        Long totalFiles = fileRepository.count(); // Actually, this counts ALL files. Let's make sure it counts only owned files!
        // Oh, wait, the total has to be for current user!
        // Yes, let's query count by owner.
        // UserRepository or StoredFileRepository count owned files. Let's count properly:
        long userTotalFiles = fileRepository.findByOwnerOrderByUploadedAtDesc(currentUser).size(); // Or write a count method
        Long userTotalSize = fileRepository.sumFileSizeByOwner(currentUser);

        List<Object[]> categoryGroups = fileRepository.getStatsByOwner(currentUser);
        Map<String, Map<String, Object>> categoryStats = new HashMap<>();

        // Initialize all categories with 0 details
        for (FileCategory cat : FileCategory.values()) {
            Map<String, Object> innerStats = new HashMap<>();
            innerStats.put("count", 0L);
            innerStats.put("size", 0L);
            innerStats.put("formattedSize", FileUtils.formatFileSize(0));
            categoryStats.put(cat.name(), innerStats);
        }

        for (Object[] group : categoryGroups) {
            FileCategory cat = (FileCategory) group[0];
            Long count = (Long) group[1];
            Long size = (Long) group[2];
            
            Map<String, Object> innerStats = new HashMap<>();
            innerStats.put("count", count);
            innerStats.put("size", size);
            innerStats.put("formattedSize", FileUtils.formatFileSize(size));
            
            categoryStats.put(cat.name(), innerStats);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalFiles", userTotalFiles);
        result.put("totalSize", userTotalSize);
        result.put("formattedTotalSize", FileUtils.formatFileSize(userTotalSize));
        result.put("categories", categoryStats);

        return result;
    }

    private StoredFile getValidatedFile(Long id) {
        StoredFile storedFile = fileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with ID: " + id));
        
        User currentUser = authService.getCurrentUser();
        if (!storedFile.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to access this file.");
        }
        return storedFile;
    }

    private FileResponse mapToFileResponse(StoredFile file) {
        return FileResponse.builder()
                .id(file.getId())
                .originalFileName(file.getOriginalFileName())
                .storedFileName(file.getStoredFileName())
                .description(file.getDescription())
                .category(file.getCategory())
                .mimeType(file.getMimeType())
                .extension(file.getExtension())
                .fileSize(file.getFileSize())
                .uploadedAt(file.getUploadedAt())
                .updatedAt(file.getUpdatedAt())
                .build();
    }
}
