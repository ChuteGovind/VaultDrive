package com.cfs.bms.util;

import com.cfs.bms.entity.FileCategory;
import org.springframework.util.StringUtils;

public class FileUtils {

    /**
     * Cleans filename to prevent path traversal and shell injection
     */
    public static String cleanFileName(String fileName) {
        if (fileName == null) {
            return "unnamed_file";
        }
        String clean = StringUtils.cleanPath(fileName);
        // Remove path traversal elements
        clean = clean.replaceAll("\\.\\./", "");
        clean = clean.replaceAll("\\.\\.\\\\", "");
        // Keep only safe characters: letters, numbers, dot, underscore, dash
        String baseName = getBaseName(clean);
        String extension = getExtension(clean);
        
        baseName = baseName.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        if (baseName.isEmpty()) {
            baseName = "file";
        }
        
        if (extension != null && !extension.isEmpty()) {
            return baseName + "." + extension.replaceAll("[^a-zA-Z0-9]", "");
        }
        return baseName;
    }

    public static String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    public static String getBaseName(String fileName) {
        if (fileName == null) {
            return "";
        }
        if (!fileName.contains(".")) {
            return fileName;
        }
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    /**
     * Determines FileCategory based on MIME type and file extension.
     */
    public static FileCategory determineCategory(String mimeType, String fileName) {
        String ext = getExtension(fileName).toLowerCase();
        
        if (mimeType != null) {
            mimeType = mimeType.toLowerCase();
            if (mimeType.startsWith("image/")) {
                return FileCategory.IMAGE;
            }
            if (mimeType.startsWith("video/")) {
                return FileCategory.VIDEO;
            }
            if (mimeType.startsWith("audio/")) {
                return FileCategory.AUDIO;
            }
            if (mimeType.equals("application/pdf")) {
                return FileCategory.PDF;
            }
            // Documents
            if (mimeType.equals("application/msword") || 
                mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                mimeType.equals("text/plain") ||
                mimeType.equals("application/rtf")) {
                return FileCategory.DOCUMENT;
            }
            // Spreadsheets
            if (mimeType.equals("application/vnd.ms-excel") || 
                mimeType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                mimeType.equals("text/csv")) {
                return FileCategory.SPREADSHEET;
            }
            // Presentations
            if (mimeType.equals("application/vnd.ms-powerpoint") || 
                mimeType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")) {
                return FileCategory.PRESENTATION;
            }
            // Archives
            if (mimeType.equals("application/zip") || 
                mimeType.equals("application/x-rar-compressed") || 
                mimeType.equals("application/x-tar") || 
                mimeType.equals("application/x-7z-compressed") ||
                mimeType.equals("application/x-gzip")) {
                return FileCategory.ARCHIVE;
            }
        }
        
        // Fallback to extensions if MIME type was vague
        switch (ext) {
            case "png": case "jpg": case "jpeg": case "gif": case "bmp": case "webp": case "svg":
                return FileCategory.IMAGE;
            case "mp4": case "avi": case "mkv": case "mov": case "webm": case "3gp":
                return FileCategory.VIDEO;
            case "mp3": case "wav": case "ogg": case "flac": case "m4a":
                return FileCategory.AUDIO;
            case "pdf":
                return FileCategory.PDF;
            case "txt": case "doc": case "docx": case "rtf": case "odt":
                return FileCategory.DOCUMENT;
            case "xls": case "xlsx": case "csv": case "ods":
                return FileCategory.SPREADSHEET;
            case "ppt": case "pptx": case "odp":
                return FileCategory.PRESENTATION;
            case "zip": case "rar": case "tar": case "gz": case "7z": case "tgz":
                return FileCategory.ARCHIVE;
            default:
                return FileCategory.OTHER;
        }
    }

    /**
     * Formats bytes to standard human-readable sizes (B, KB, MB, GB)
     */
    public static String formatFileSize(long size) {
        if (size <= 0) {
            return "0 B";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format("%.2f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }
}
