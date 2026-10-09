package com.smartstay.tenant.response.invoices;

import java.util.List;

public record WalletInfoRes(Integer totalWalletItems,
                            Double totalWalletAmount,
                            List<WalletItemsRes> walletItems) {
}
