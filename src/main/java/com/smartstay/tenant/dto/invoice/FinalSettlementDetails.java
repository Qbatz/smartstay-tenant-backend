package com.smartstay.tenant.dto.invoice;

import com.smartstay.tenant.response.eb.InvoiceEbResponse;
import com.smartstay.tenant.response.invoices.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinalSettlementDetails {

    private String invoiceId;
    private String invoiceNumber;
    private String invoiceType;
    private String generatedDate;
    private String dueDate;
    private String startDate;
    private String endDate;
    private Double totalAmount;
    private Double discountAmount;
    private Double paidAmount;
    private Double dueAmount;
    private Double deductionAmount;
    private String status;
    private String paymentStatus;
    private Double gst;
    private Double cgst;
    private Double sgst;
    private Double gstPercentile;
    private List<DeductionsRes> deductions;
    private List<InvoiceItemDTO> invoiceItems;
    private List<ReceiptDTO> receipts;
    private AdvanceInfo advanceInfo;
    private CurrentMonthInfo currentMonthInfo;
    private List<UnpaidInvoices> unpaidInvoices;
    private InvoiceEbResponse ebInfo;
    private String lastPaidDate;
    private String lastPaymentMode;
    private String lastReferenceId;
    private Boolean showMessage;
    private SettlementRetainerInfoRes settlementRetainerInfo;
    private SettlementAdditionalAdvance settlementAdditionalAdvance;
    private CustomerBookingInfoRes customerBookingInfo;
    private CustomerAdvanceInfoRes customerAdvanceInfo;
    private WalletInfoRes walletInfo;
    private UnpaidInvoiceInfo unpaidInvoiceInfo;
    private CurrentRentInfo currentMonthRentInfo;
    private CurrentMonthEbInfo currentMonthEbInfo;
}
