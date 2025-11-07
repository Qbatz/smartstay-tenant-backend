package com.smartstay.tenant.service;

import com.smartstay.tenant.repository.InvoicesV1Repository;
import com.smartstay.tenant.response.hostel.InvoiceItems;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class InvoiceService {

    private final InvoicesV1Repository invoicesV1Repository;

    public InvoiceService(InvoicesV1Repository invoicesV1Repository) {
        this.invoicesV1Repository = invoicesV1Repository;
    }

    public List<InvoiceItems> getInvoicesWithItems(String customerId, Date startDate, Date endDate) {
        return invoicesV1Repository.findInvoiceItemsWithTransactionsByCustomerAndDateRange(customerId, startDate, endDate);
    }
}
