package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.InvoiceRedemption;
import com.smartstay.tenant.repository.InvoiceRedemptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceRedemptionService {

    @Autowired
    private InvoiceRedemptionRepository invoiceRedemptionRepository;

    public List<InvoiceRedemption> getInvoiceRedemptionByInvoiceId(String invoiceId){
        return invoiceRedemptionRepository.findByInvoiceId(invoiceId);
    }
}
