package com.smartstay.tenant.response.eb;

import java.util.List;

public record InvoiceEbResponse(Double unitPrice,
                                String typeOfReading,
                                Double pendingEbAmount,
                                List<EbReadingsResponse> ebReadings) {
}
