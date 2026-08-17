package com.smartstay.tenant.mapper.invoice;

import com.smartstay.tenant.Utils.InvoiceUtils;
import com.smartstay.tenant.dto.invoice.InvoiceItemProjection;
import com.smartstay.tenant.dto.invoice.InvoiceItemResponseDTO;
import com.smartstay.tenant.ennum.PaymentStatus;

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

        boolean canShowPaymentDate = false;
        if (PaymentStatus.PAID.name().equals(invoiceItemProjection.getStatus()) ||
                PaymentStatus.PARTIAL_PAYMENT.name().equals(invoiceItemProjection.getStatus())) {
            canShowPaymentDate = true;
        }

        return new InvoiceItemResponseDTO(
                invoiceItemProjection.getInvoiceId(),
                invoiceItemProjection.getInvoiceType(),
                invoiceItemProjection.getInvoiceNumber(),
                invoiceItemProjection.getTotalAmount(),
                invoiceItemProjection.getDiscountAmount(),
                invoiceItemProjection.getInvoiceDueDate(),
                invoiceItemProjection.getInvoiceGeneratedDate(),
                invoiceItemProjection.getInvoiceStartDate(),
                invoiceItemProjection.getInvoiceEndDate(),
                invoiceItemProjection.getPaidAt(),
                invoiceItemProjection.getPaymentDate(),
                invoiceItemProjection.getPaidAmount(),
                invoiceItemProjection.getDueAmount(),
                status,
                invoiceItemProjection.getIsCancelled(),
                canShowPaymentDate
        );
    }
}
