package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.BankingMethods;
import com.smartstay.tenant.repository.BankingMethodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankingMethodsService {

    @Autowired
    private BankingMethodsRepository bankingMethodsRepository;

    public BankingMethods getByPaymentMethodId(String paymentMethodId) {
        return bankingMethodsRepository.findByPaymentMethodId(paymentMethodId);
    }
}
