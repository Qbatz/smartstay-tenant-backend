package com.smartstay.tenant.Utils;

import com.smartstay.tenant.ennum.PaymentStatus;

public class InvoiceUtils {

    public static String getInvoicePaymentStatusByStatus(String status) {
        if (status != null) {
            String paymentStatus = null;
            if (status.equalsIgnoreCase(PaymentStatus.PAID.name())) {
                paymentStatus = "Paid";
            }
            else if (status.equalsIgnoreCase(PaymentStatus.PENDING.name())) {
                paymentStatus = "Pending";
            }
            else if (status.equalsIgnoreCase(PaymentStatus.PARTIAL_PAYMENT.name())) {
                paymentStatus = "Partial Payment";
            }
            else if (status.equalsIgnoreCase(PaymentStatus.ADVANCE_IN_HAND.name())) {
                paymentStatus = "Over pay";
            }
            else if (status.equalsIgnoreCase(PaymentStatus.REFUNDED.name())) {
                paymentStatus = "Refunded";
            }
            else if (status.equalsIgnoreCase(PaymentStatus.PENDING_REFUND.name())) {
                paymentStatus = "Pending Refund";
            }
            else if (status.equalsIgnoreCase(PaymentStatus.PARTIAL_REFUND.name())) {
                paymentStatus = "Partially Refunded";
            }

            return paymentStatus;
        }
        return null;
    }
}
