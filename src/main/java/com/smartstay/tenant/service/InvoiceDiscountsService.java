package com.smartstay.tenant.service;

import com.smartstay.tenant.repository.InvoiceDiscountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceDiscountsService {

    @Autowired
    private InvoiceDiscountsRepository invoiceDiscountsRepository;

    public double getDiscountAmountByInvoiceId(String invoiceId) {
        Double invoiceDiscountAmount = invoiceDiscountsRepository
                .findDiscountAmountByInvoiceId(invoiceId);
        return invoiceDiscountAmount != null ? invoiceDiscountAmount : 0;
    }
}
