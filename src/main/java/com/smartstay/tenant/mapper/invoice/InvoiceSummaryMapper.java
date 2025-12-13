package com.smartstay.tenant.mapper.invoice;

import com.smartstay.tenant.dto.invoice.InvoiceSummaryProjection;
import com.smartstay.tenant.response.dashboard.InvoiceSummaryResponse;

import java.util.function.Function;

public class InvoiceSummaryMapper
        implements Function<InvoiceSummaryProjection, InvoiceSummaryResponse> {

    @Override
    public InvoiceSummaryResponse apply(InvoiceSummaryProjection p) {

        if (p == null) {
            return null;
        }

        return new InvoiceSummaryResponse(
                p.getRentAmount(),
                p.getEbAmount(),
                p.getPaidAmount(),
                p.getInvoiceNumber(),
                p.getInvoiceGeneratedDate(),
                p.getInvoiceDueDate(),
                p.getInvoiceStartDate(),
                p.getInvoiceEndDate()
        );
    }
}

