package com.smartstay.tenant.Utils;

import com.smartstay.tenant.dto.files.FileDetails;

import java.net.URI;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class FileUtils {

    public static FileDetails getFileDetailsFromUrl(String url) {

        if (url == null || url.isBlank()) {
            return null;
        }

        String fileName = null;

        // URI parsing
        try {
            URI uri = new URI(url);
            String path = uri.getPath();

            if (path != null && path.contains("/")) {
                fileName = Paths.get(path).getFileName().toString();
                fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
            }
        } catch (Exception ignored) {
        }

        // Fallback
        if (fileName == null || fileName.isBlank()) {
            try {
                String cleanUrl = url.split("\\?")[0];
                fileName = cleanUrl.substring(cleanUrl.lastIndexOf('/') + 1);
            } catch (Exception ignored) {
            }
        }

        if (fileName == null || fileName.isBlank()) {
            return null;
        }

        // Extension
        String extension = "";
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            extension = fileName.substring(lastDot + 1);
        }

        // Base name without extension
        String logicalName = fileName;
        if (lastDot > 0) {
            logicalName = fileName.substring(0, lastDot);
        }

        // Remove UUID/timestamp prefix if present
        int lastUnderscore = logicalName.lastIndexOf('_');
        if (lastUnderscore > 0 && lastUnderscore < logicalName.length() - 1) {
            logicalName = logicalName.substring(lastUnderscore + 1);
        }

        // File size
        Long size = null;
        try {
            URLConnection connection = URI.create(url).toURL().openConnection();
            size = (long) connection.getContentLength();
            if (size < 0) {
                size = null;
            }
        } catch (Exception ignored) {
        }

        return new FileDetails(logicalName, extension, size);
    }

    public static String formatFileSize(Long sizeInBytes) {

        if (sizeInBytes == null || sizeInBytes <= 0) {
            return "0 B";
        }

        final String[] units = {"B", "KB", "MB", "GB", "TB"};
        double size = sizeInBytes;
        int unitIndex = 0;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        // Show decimals only when needed
        if (size % 1 == 0) {
            return String.format("%.0f %s", size, units[unitIndex]);
        }

        return String.format("%.1f %s", size, units[unitIndex]);
    }
}
