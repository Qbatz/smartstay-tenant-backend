package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.CustomerCredentials;
import com.smartstay.tenant.dao.CustomersOtp;
import com.smartstay.tenant.repository.CustomersOtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CustomersOtpService {

    @Autowired
    private CustomersOtpRepository customersOtpRepository;

    public CustomersOtp createNewCustomersOtp(CustomerCredentials customerCredentials) {

        if (customerCredentials == null) {
            return null;
        }

        Date now = new Date();
        Date expiryAt = new Date(now.getTime() + (15 * 60 * 1000));
        int otp = generateSixDigitOtp();

        CustomersOtp customersOtp = customersOtpRepository
                .findByXuid(customerCredentials.getXuid());

        if (customersOtp != null) {
            customersOtp.setUpdatedAt(new Date());
        } else {
            customersOtp = new CustomersOtp();
            customersOtp.setXuid(customerCredentials.getXuid());
            customersOtp.setCreatedAt(new Date());
        }

        customersOtp.setOtp(otp);
        customersOtp.setExpiryAt(expiryAt);
        customersOtp.setVerified(false);

        return customersOtpRepository.save(customersOtp);
    }

    private int generateSixDigitOtp() {
        return (int) (100000 + Math.random() * 900000);
    }

    public CustomersOtp getByXuid(String xuid) {
        return customersOtpRepository.findByXuid(xuid);
    }

    public CustomersOtp save(CustomersOtp customersOtp) {
        return customersOtpRepository.save(customersOtp);
    }
}