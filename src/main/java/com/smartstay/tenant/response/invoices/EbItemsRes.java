package com.smartstay.tenant.response.invoices;

import java.util.Date;

public record EbItemsRes(//from EB readings
                         Integer readingId,
                         //from customer eb history
                         Long customerEBId,
                         Date fromDate,
                         Date toDate,
                         Double totalAmount,
                         //same as units in customer eb history
                         Double consumption) {
}
