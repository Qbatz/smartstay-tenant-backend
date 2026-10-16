package com.smartstay.tenant.mapper.invoice;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dto.invoice.InvoiceSummaryProjection;
import com.smartstay.tenant.response.dashboard.InvoiceSummaryResponse;

import java.util.function.Function;

public class InvoiceSummaryMapper implements Function<InvoiceSummaryProjection, InvoiceSummaryResponse> {

    @Override
    public InvoiceSummaryResponse apply(InvoiceSummaryProjection p) {

        if (p == null) {
            return null;
        }

        String invoiceDueDate = null;
        String invoiceGeneratedDate = null;
        String invoiceStartDate = null;
        String invoiceEndDate = null;
        String invoicePaymentDate = null;

        if (p.getInvoiceDueDate() != null) {
            invoiceDueDate = Utils.dateToString(Utils.localDateToDate(p.getInvoiceDueDate()));
        }
        if (p.getInvoiceGeneratedDate() != null) {
            invoiceGeneratedDate = Utils.dateToString(Utils.localDateToDate(p.getInvoiceGeneratedDate()));
        }
        if (p.getInvoiceStartDate() != null) {
            invoiceStartDate = Utils.dateToString(Utils.localDateToDate(p.getInvoiceStartDate()));
        }
        if (p.getInvoiceEndDate() != null) {
            invoiceEndDate = Utils.dateToString(Utils.localDateToDate(p.getInvoiceEndDate()));
        }
        if (p.getPaymentDate() != null) {
            invoicePaymentDate = Utils.dateToString(Utils.localDateToDate(p.getPaymentDate()));
        }

        return new InvoiceSummaryResponse(
                p.getRentAmount(),
                p.getEbAmount(),
                p.getDiscountAmount(),
                p.getPaidAmount(),
                p.getInvoiceNumber(),
                invoiceGeneratedDate,
                invoiceDueDate,
                invoiceStartDate,
                invoiceEndDate,
                p.getPaymentStatus(),
                invoicePaymentDate
        );
    }
}

