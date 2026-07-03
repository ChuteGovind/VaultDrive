package com.cfs.bms.dto;

import com.cfs.bms.entity.FileCategory;
import java.time.LocalDateTime;

public class FileResponse {
    private Long id;
    private String originalFileName;
    private String storedFileName;
    private String description;
    private FileCategory category;
    private String mimeType;
    private String extension;
    private Long fileSize;
    private LocalDateTime uploadedAt;
    private LocalDateTime updatedAt;

    public FileResponse() {}

    public FileResponse(Long id, String originalFileName, String storedFileName, String description, FileCategory category, String mimeType, String extension, Long fileSize, LocalDateTime uploadedAt, LocalDateTime updatedAt) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.description = description;
        this.category = category;
        this.mimeType = mimeType;
        this.extension = extension;
        this.fileSize = fileSize;
        this.uploadedAt = uploadedAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public String getStoredFileName() { return storedFileName; }
    public void setStoredFileName(String storedFileName) { this.storedFileName = storedFileName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public FileCategory getCategory() { return category; }
    public void setCategory(FileCategory category) { this.category = category; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Builder Pattern
    public static FileResponseBuilder builder() {
        return new FileResponseBuilder();
    }

    public static class FileResponseBuilder {
        private Long id;
        private String originalFileName;
        private String storedFileName;
        private String description;
        private FileCategory category;
        private String mimeType;
        private String extension;
        private Long fileSize;
        private LocalDateTime uploadedAt;
        private LocalDateTime updatedAt;

        FileResponseBuilder() {}

        public FileResponseBuilder id(Long id) { this.id = id; return this; }
        public FileResponseBuilder originalFileName(String originalFileName) { this.originalFileName = originalFileName; return this; }
        public FileResponseBuilder storedFileName(String storedFileName) { this.storedFileName = storedFileName; return this; }
        public FileResponseBuilder description(String description) { this.description = description; return this; }
        public FileResponseBuilder category(FileCategory category) { this.category = category; return this; }
        public FileResponseBuilder mimeType(String mimeType) { this.mimeType = mimeType; return this; }
        public FileResponseBuilder extension(String extension) { this.extension = extension; return this; }
        public FileResponseBuilder fileSize(Long fileSize) { this.fileSize = fileSize; return this; }
        public FileResponseBuilder uploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; return this; }
        public FileResponseBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public FileResponse build() {
            return new FileResponse(id, originalFileName, storedFileName, description, category, mimeType, extension, fileSize, uploadedAt, updatedAt);
        }
    }
}
