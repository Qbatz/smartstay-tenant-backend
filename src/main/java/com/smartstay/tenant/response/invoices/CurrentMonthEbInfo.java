package com.smartstay.tenant.response.invoices;

import java.util.List;

public record CurrentMonthEbInfo(double currentMonthEbAmount,
                                 List<EbItemsRes> ebItems) {
}
