package com.smartstay.tenant.response.hostel;

import java.util.Date;

public interface InvoiceItems {
    String getInvoiceItem();
    Double getAmount();
    Date getPaidDate();
    Double getPaidAmount();
}
