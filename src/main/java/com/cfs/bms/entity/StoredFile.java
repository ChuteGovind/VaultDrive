package com.cfs.bms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stored_files")
public class StoredFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String storedFileName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileCategory category;

    @Column(nullable = false)
    private String mimeType;

    private String extension;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String storagePath;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Default Constructor
    public StoredFile() {}

    // Convenience All-Args Constructor
    public StoredFile(Long id, String originalFileName, String storedFileName, String description, FileCategory category, String mimeType, String extension, Long fileSize, String storagePath, LocalDateTime uploadedAt, LocalDateTime updatedAt, User owner) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.description = description;
        this.category = category;
        this.mimeType = mimeType;
        this.extension = extension;
        this.fileSize = fileSize;
        this.storagePath = storagePath;
        this.uploadedAt = uploadedAt;
        this.updatedAt = updatedAt;
        this.owner = owner;
    }

    // Getters and Setters
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

    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    // Builder Pattern
    public static StoredFileBuilder builder() {
        return new StoredFileBuilder();
    }

    public static class StoredFileBuilder {
        private Long id;
        private String originalFileName;
        private String storedFileName;
        private String description;
        private FileCategory category;
        private String mimeType;
        private String extension;
        private Long fileSize;
        private String storagePath;
        private LocalDateTime uploadedAt;
        private LocalDateTime updatedAt;
        private User owner;

        StoredFileBuilder() {}

        public StoredFileBuilder id(Long id) { this.id = id; return this; }
        public StoredFileBuilder originalFileName(String originalFileName) { this.originalFileName = originalFileName; return this; }
        public StoredFileBuilder storedFileName(String storedFileName) { this.storedFileName = storedFileName; return this; }
        public StoredFileBuilder description(String description) { this.description = description; return this; }
        public StoredFileBuilder category(FileCategory category) { this.category = category; return this; }
        public StoredFileBuilder mimeType(String mimeType) { this.mimeType = mimeType; return this; }
        public StoredFileBuilder extension(String extension) { this.extension = extension; return this; }
        public StoredFileBuilder fileSize(Long fileSize) { this.fileSize = fileSize; return this; }
        public StoredFileBuilder storagePath(String storagePath) { this.storagePath = storagePath; return this; }
        public StoredFileBuilder uploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; return this; }
        public StoredFileBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public StoredFileBuilder owner(User owner) { this.owner = owner; return this; }

        public StoredFile build() {
            return new StoredFile(id, originalFileName, storedFileName, description, category, mimeType, extension, fileSize, storagePath, uploadedAt, updatedAt, owner);
        }
    }
}
