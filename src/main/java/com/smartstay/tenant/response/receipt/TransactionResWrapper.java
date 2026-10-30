package com.smartstay.tenant.response.receipt;

import com.smartstay.tenant.dto.TransactionDto;
import com.smartstay.tenant.response.PaginationDataRes;

import java.util.List;

public record TransactionResWrapper(PaginationDataRes paginationData,
                                    List<TransactionDto> transactions) {
}
