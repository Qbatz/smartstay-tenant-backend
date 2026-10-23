package com.smartstay.tenant.Utils;

import com.smartstay.tenant.dao.BankingMethods;
import com.smartstay.tenant.dao.BankingV2;
import com.smartstay.tenant.ennum.BankAccountType;

public class BankUtils {

    public static String getBankName(BankingV2 bankingV2, BankingMethods bankingMethods){

        if (bankingV2 != null) {
            if (BankAccountType.CASH.name().equalsIgnoreCase(bankingV2.getAccountType()) ||
                    BankAccountType.CARD.name().equalsIgnoreCase(bankingV2.getAccountType())) {

                StringBuilder builder = new StringBuilder();

                if (bankingV2.getDisplayName() != null && !bankingV2.getDisplayName().isBlank()) {
                    builder.append(bankingV2.getDisplayName());
                }
                if (!builder.toString().isBlank()){
                    builder.append("-");
                }
                if (bankingV2.getBankAccountType() != null && !bankingV2.getBankAccountType().isBlank()) {
                    builder.append(bankingV2.getBankAccountType());
                }

                if (builder.toString().isBlank()) {
                    return bankingV2.getBankName() != null && !bankingV2.getBankName().isBlank()
                            ? bankingV2.getBankName() : null;
                }

                return builder.toString();

            } else {

                if (bankingMethods != null){

                    StringBuilder builder = new StringBuilder();

                    if (bankingMethods.getPaymentMethod() != null && !bankingMethods.getPaymentMethod().isBlank()) {
                        builder.append(bankingMethods.getPaymentMethod());
                    }
                    if (!builder.toString().isBlank()){
                        builder.append("-");
                    }
                    if (bankingMethods.getDisplayName() != null && !bankingMethods.getDisplayName().isBlank()) {
                        builder.append(bankingMethods.getDisplayName());
                    }

                    if (builder.toString().isBlank()) {
                        return bankingV2.getBankName() != null && !bankingV2.getBankName().isBlank()
                                ? bankingV2.getBankName() : null;
                    }

                    return builder.toString();

                } else {
                    return bankingV2.getBankName() != null && !bankingV2.getBankName().isBlank()
                            ? bankingV2.getBankName() : null;
                }
            }
        }

        return null;
    }

    public static String formatBankName(BankingV2 bankingV2) {

        if (bankingV2 == null || bankingV2.getAccountType() == null
                || bankingV2.getAccountType().isBlank()) {
            return null;
        }

        if (BankAccountType.CASH.name().equalsIgnoreCase(bankingV2.getAccountType())) {
            return "Cash";
        } else if (BankAccountType.CARD.name().equalsIgnoreCase(bankingV2.getAccountType())) {
            return "Card";
        } else if (BankAccountType.BANK.name().equalsIgnoreCase(bankingV2.getAccountType())) {
            return "Bank";
        } else if (BankAccountType.UPI.name().equalsIgnoreCase(bankingV2.getAccountType())) {
            return "UPI";
        } else {
            return bankingV2.getBankName() != null && !bankingV2.getBankName().isBlank()
                    ? Utils.capitalize(bankingV2.getBankName()) : null;
        }
    }

    public static String formatPaymentMode(BankingV2 bankingV2) {

        if (bankingV2 != null){
            if (bankingV2.getAccountType() != null){
                if (BankAccountType.CASH.name().equalsIgnoreCase(bankingV2.getAccountType())){
                    return  "Cash";
                } else if (BankAccountType.CARD.name().equalsIgnoreCase(bankingV2.getAccountType())){
                    return  "Card";
                }  else if (BankAccountType.UPI.name().equalsIgnoreCase(bankingV2.getAccountType())){
                    return  "Upi";
                }  else if (BankAccountType.BANK.name().equalsIgnoreCase(bankingV2.getAccountType())){
                    return  "Bank";
                } else {
                    return Utils.capitalize(bankingV2.getAccountType());
                }
            }
        }

        return null;
    }
}
