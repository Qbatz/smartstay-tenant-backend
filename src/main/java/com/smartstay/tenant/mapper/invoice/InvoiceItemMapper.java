package com.smartstay.tenant.mapper.invoice;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dto.invoice.InvoiceItemProjection;
import com.smartstay.tenant.dto.invoice.InvoiceItemResponseDTO;

import java.util.function.Function;

public class InvoiceItemMapper implements Function<InvoiceItemProjection, InvoiceItemResponseDTO> {

    @Override
    public InvoiceItemResponseDTO apply(InvoiceItemProjection invoiceItemProjection) {

        if (invoiceItemProjection == null) {
            return null;
        }
        return new InvoiceItemResponseDTO(
                invoiceItemProjection.getInvoiceId(),
                invoiceItemProjection.getInvoiceType(),
                invoiceItemProjection.getInvoiceNumber(),
                invoiceItemProjection.getTotalAmount(),
                invoiceItemProjection.getInvoiceDueDate(),
                invoiceItemProjection.getInvoiceGeneratedDate(),
                invoiceItemProjection.getInvoiceStartDate(),
                invoiceItemProjection.getPaidAmount(),
                invoiceItemProjection.getDueAmount(),
                Utils.capitalize(invoiceItemProjection.getStatus())
        );
    }
}
