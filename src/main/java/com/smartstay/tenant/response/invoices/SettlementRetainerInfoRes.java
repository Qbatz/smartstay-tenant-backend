package com.smartstay.tenant.response.invoices;

import java.util.List;

public record SettlementRetainerInfoRes(int totalAppliedRetainers,
                                        Double totalRetainerBalanceAmount,
                                        List<RetainerInfoRes> retainerInfos) {
}
