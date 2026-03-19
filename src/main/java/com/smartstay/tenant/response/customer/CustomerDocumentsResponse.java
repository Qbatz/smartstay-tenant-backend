package com.smartstay.tenant.response.customer;

public record CustomerDocumentsResponse(Long documentId,
                                        String documentType,
                                        String documentUrl,
                                        String documentFileType) {
}
