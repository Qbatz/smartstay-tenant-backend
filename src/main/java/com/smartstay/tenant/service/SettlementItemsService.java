package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.SettlementItems;
import com.smartstay.tenant.repository.SettlementItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettlementItemsService {

    @Autowired
    private SettlementItemsRepository settlementItemsRepository;

    public SettlementItems getByInvoiceId(String invoiceId) {
        return settlementItemsRepository.findByInvoiceId(invoiceId);
    }
}
