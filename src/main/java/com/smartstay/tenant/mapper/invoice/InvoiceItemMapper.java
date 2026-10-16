package com.smartstay.tenant.mapper.invoice;

import com.smartstay.tenant.Utils.InvoiceUtils;
import com.smartstay.tenant.Utils.Utils;
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

        String adjustmentStatus = null;
        String lastAdjustmentDate = null;
        String lastAdjustmentTime = null;

        double balanceAmount = 0;

        String invoiceDueDate = null;
        String invoiceGeneratedDate = null;
        String invoiceStartDate = null;
        String invoiceEndDate = null;
        String invoicePaidAtDate = null;
        String invoicePaymentDate = null;

        if (invoiceItemProjection.getInvoiceDueDate() != null) {
            invoiceDueDate = Utils.dateToString(Utils.localDateToDate(invoiceItemProjection.getInvoiceDueDate()));
        }
        if (invoiceItemProjection.getInvoiceGeneratedDate() != null) {
            invoiceGeneratedDate = Utils.dateToString(Utils.localDateToDate(invoiceItemProjection.getInvoiceGeneratedDate()));
        }
        if (invoiceItemProjection.getInvoiceStartDate() != null) {
            invoiceStartDate = Utils.dateToString(Utils.localDateToDate(invoiceItemProjection.getInvoiceStartDate()));
        }
        if (invoiceItemProjection.getInvoiceEndDate() != null) {
            invoiceEndDate = Utils.dateToString(Utils.localDateToDate(invoiceItemProjection.getInvoiceEndDate()));
        }
        if (invoiceItemProjection.getPaidAt() != null) {
            invoicePaidAtDate = Utils.dateToString(Utils.localDateToDate(invoiceItemProjection.getPaidAt()));
        }
        if (invoiceItemProjection.getPaymentDate() != null) {
            invoicePaymentDate = Utils.dateToString(Utils.localDateToDate(invoiceItemProjection.getPaymentDate()));
        }

        return new InvoiceItemResponseDTO(
                invoiceItemProjection.getInvoiceId(),
                invoiceItemProjection.getInvoiceType(),
                invoiceItemProjection.getInvoiceNumber(),
                invoiceItemProjection.getTotalAmount(),
                invoiceItemProjection.getDiscountAmount(),
                invoiceDueDate,
                invoiceGeneratedDate,
                invoiceStartDate,
                invoiceEndDate,
                invoicePaidAtDate,
                invoicePaymentDate,
                invoiceItemProjection.getPaidAmount(),
                balanceAmount,
                invoiceItemProjection.getDueAmount(),
                status,
                invoiceItemProjection.getIsCancelled(),
                canShowPaymentDate,
                adjustmentStatus,
                lastAdjustmentDate,
                lastAdjustmentTime
        );
    }
}
