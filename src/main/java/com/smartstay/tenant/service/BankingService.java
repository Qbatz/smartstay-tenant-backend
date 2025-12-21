package com.smartstay.tenant.service;


import com.smartstay.tenant.dao.BankingV1;
import com.smartstay.tenant.repository.BankingV1Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankingService {


    @Autowired
    private BankingV1Repository bankingV1Repository;


    public BankingV1 getBankDetails(String bankAccountId) {
        return bankingV1Repository.findByBankId(bankAccountId);
    }
}
