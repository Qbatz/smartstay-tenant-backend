package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.InvoiceDiscounts;
import com.smartstay.tenant.repository.InvoiceDiscountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class InvoiceDiscountsService {

    @Autowired
    private InvoiceDiscountsRepository invoiceDiscountsRepository;

    public double getDiscountAmountByInvoiceId(String invoiceId) {
        Double invoiceDiscountAmount = invoiceDiscountsRepository
                .findDiscountAmountByInvoiceId(invoiceId);
        return invoiceDiscountAmount != null ? invoiceDiscountAmount : 0;
    }

    public List<InvoiceDiscounts> getInvoiceDiscountsByInvoiceIds(Set<String> invoiceIds) {
        return invoiceDiscountsRepository
                .findAllByInvoiceIdInAndIsActiveTrue(invoiceIds);
    }
}
