package com.cfs.bms.service;

import com.cfs.bms.exception.FileStorageException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class StorageService {

    private final Path rootLocation = Paths.get("uploads");

    public StorageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new FileStorageException("Could not initialize root upload directory", e);
        }
    }

    /**
     * Stores a physical file in uploads/{userId}/
     * Returns the unique stored filename and resolved target path as a string.
     */
    public String[] store(MultipartFile file, Long userId) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Failed to store empty file.");
        }
        
        try {
            Path userDirectory = rootLocation.resolve(String.valueOf(userId)).normalize();
            Files.createDirectories(userDirectory);

            String originalName = file.getOriginalFilename();
            if (originalName == null || originalName.contains("..")) {
                throw new IllegalArgumentException("Cannot store file with relative path entry " + originalName);
            }

            // Generate unique name
            String fileExtension = getFileExtension(originalName);
            String uniqueName = UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + (fileExtension.isEmpty() ? "" : "." + fileExtension);
            
            Path destinationFile = userDirectory.resolve(Paths.get(uniqueName)).normalize().toAbsolutePath();
            
            // Path traversal guard
            if (!destinationFile.getParent().equals(userDirectory.toAbsolutePath())) {
                throw new SecurityException("Cannot store file outside current user directory.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            
            return new String[]{uniqueName, destinationFile.toString()};
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file.", e);
        }
    }

    /**
     * Load path of a physical file
     */
    public Path load(String storedFileName, Long userId) {
        Path userDirectory = rootLocation.resolve(String.valueOf(userId)).normalize();
        Path file = userDirectory.resolve(storedFileName).normalize();
        
        if (!file.toAbsolutePath().getParent().equals(userDirectory.toAbsolutePath())) {
            throw new SecurityException("Access denied - path traversal detected.");
        }
        
        if (!Files.exists(file)) {
            throw new FileStorageException("Physical file not found on server.");
        }
        return file;
    }

    /**
     * Deletes a physical file
     */
    public void delete(String storedFileName, Long userId) {
        try {
            Path file = load(storedFileName, userId);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new FileStorageException("Failed to delete physical file", e);
        } catch (Exception e) {
             // If load fails because the file doesn't exist, we ignore or log
        }
    }

    private String getFileExtension(String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return name.substring(lastIndexOf + 1);
    }
}
