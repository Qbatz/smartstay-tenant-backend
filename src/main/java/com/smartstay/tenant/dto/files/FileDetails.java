package com.smartstay.tenant.dto.files;

public record FileDetails(String fileName,
                          String extension,
                          Long sizeInBytes) {
}
