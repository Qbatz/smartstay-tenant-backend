package com.smartstay.tenant.response.customer;

public record CustomerHostelDocsRes(Long documentId,
                                    String docType,
                                    String docFileType,
                                    String docFileExtType,
                                    String docFileName,
                                    String docFileSize,
                                    String docFileUrl) {
}
