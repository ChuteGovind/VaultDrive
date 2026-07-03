package com.cfs.bms.controller;

import com.cfs.bms.dto.FileResponse;
import com.cfs.bms.dto.FileUpdateRequest;
import com.cfs.bms.entity.FileCategory;
import com.cfs.bms.service.FileService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category", required = false) FileCategory category) {
        
        FileResponse responseDto = fileService.uploadFile(file, description, category);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FileResponse>> getAllFiles() {
        List<FileResponse> files = fileService.getUserFiles();
        return ResponseEntity.ok(files);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<FileResponse>> getRecentFiles() {
        List<FileResponse> files = fileService.getRecentFiles();
        return ResponseEntity.ok(files);
    }

    @GetMapping("/search")
    public ResponseEntity<List<FileResponse>> searchFiles(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "category", required = false) FileCategory category,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<FileResponse> results = fileService.searchFiles(name, category, startDate, endDate);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileResponse> getFileDetails(@PathVariable Long id) {
        FileResponse responseDto = fileService.getFileDetails(id);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        Resource resource = fileService.loadFileAsResource(id);
        FileResponse responseDto = fileService.getFileDetails(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + responseDto.getOriginalFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<Resource> previewFile(@PathVariable Long id) {
        Resource resource = fileService.loadFileAsResource(id);
        FileResponse responseDto = fileService.getFileDetails(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, responseDto.getMimeType())
                // Ensure browser doesn't execute uploaded source/script files, use sandbox or force download for HTML/JS if needed,
                // but standard images, audio, video and pdf open fine.
                .header("Content-Security-Policy", "default-src 'none'; media-src 'self'; img-src 'self'; style-src 'unsafe-inline';")
                .body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FileResponse> updateFile(
            @PathVariable Long id,
            @Valid @RequestBody FileUpdateRequest request) {
        
        FileResponse responseDto = fileService.updateFile(id, request);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id) {
        fileService.deleteFile(id);
        return ResponseEntity.ok(Map.of("message", "File deleted successfully"));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = fileService.getStorageStats();
        return ResponseEntity.ok(stats);
    }
}
