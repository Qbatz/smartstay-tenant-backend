package com.smartstay.tenant.mapper.invoice;

import com.smartstay.tenant.Utils.InvoiceUtils;
import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dto.invoice.InvoiceItemProjection;
import com.smartstay.tenant.dto.invoice.InvoiceItemResponseDTO;

import java.time.LocalDate;
import java.util.Date;
import java.util.function.Function;

public class InvoiceItemMapper implements Function<InvoiceItemProjection, InvoiceItemResponseDTO> {

    @Override
    public InvoiceItemResponseDTO apply(InvoiceItemProjection invoiceItemProjection) {

        if (invoiceItemProjection == null) {
            return null;
        }
        String status = InvoiceUtils.getInvoicePaymentStatusByStatus(invoiceItemProjection.getStatus());
        if (invoiceItemProjection.getIsCancelled() != null && invoiceItemProjection.getIsCancelled()) {
            status = "Cancelled";
        }
        return new InvoiceItemResponseDTO(
                invoiceItemProjection.getInvoiceId(),
                invoiceItemProjection.getInvoiceType(),
                invoiceItemProjection.getInvoiceNumber(),
                invoiceItemProjection.getTotalAmount(),
                invoiceItemProjection.getInvoiceDueDate(),
                invoiceItemProjection.getInvoiceGeneratedDate(),
                invoiceItemProjection.getInvoiceStartDate(),
                invoiceItemProjection.getPaidAt(),
                invoiceItemProjection.getPaymentDate(),
                invoiceItemProjection.getPaidAmount(),
                invoiceItemProjection.getDueAmount(),
                status,
                invoiceItemProjection.getIsCancelled()
        );
    }
}
