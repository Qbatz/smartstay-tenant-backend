package com.smartstay.tenant.response;

public record PaginationDataRes(int currentPage,
                                int pageSize,
                                long totalElements,
                                int totalPages) {
}
