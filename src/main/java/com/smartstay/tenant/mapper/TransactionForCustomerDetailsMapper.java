package com.smartstay.tenant.mapper;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dao.TransactionV1;
import com.smartstay.tenant.dto.TransactionDto;

import java.util.function.Function;

public class TransactionForCustomerDetailsMapper implements Function<TransactionV1, TransactionDto> {
    @Override
    public TransactionDto apply(TransactionV1 transactionV1) {
        return new TransactionDto(transactionV1.getTransactionId(),
                transactionV1.getInvoiceId(),
                Utils.dateToString(transactionV1.getPaymentDate()),
                transactionV1.getPaidAmount(),
                transactionV1.getReferenceNumber(),
                transactionV1.getBankId(),
                transactionV1.getCreatedBy());
    }
}

