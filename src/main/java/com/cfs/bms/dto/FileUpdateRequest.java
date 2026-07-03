package com.cfs.bms.dto;

import com.cfs.bms.entity.FileCategory;
import jakarta.validation.constraints.NotBlank;

public class FileUpdateRequest {
    
    @NotBlank(message = "File name cannot be empty")
    private String originalFileName;
    
    private String description;
    
    private FileCategory category;

    public FileUpdateRequest() {}

    public FileUpdateRequest(String originalFileName, String description, FileCategory category) {
        this.originalFileName = originalFileName;
        this.description = description;
        this.category = category;
    }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public FileCategory getCategory() { return category; }
    public void setCategory(FileCategory category) { this.category = category; }
}
