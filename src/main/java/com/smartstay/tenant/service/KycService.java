package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.dao.HostelV1;
import com.smartstay.tenant.dao.KycDetails;
import com.smartstay.tenant.dto.kyc.DigioKycResponse;
import com.smartstay.tenant.ennum.CustomerStatus;
import com.smartstay.tenant.ennum.KycStatus;
import com.smartstay.tenant.repository.KycDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class KycService {

    @Value("${DIGIO_URL}")
    private String digioUrl;
    @Value("${DIGIO_USERNAME}")
    private String digioUserName;
    @Value("${DIGIO_PASSWORD}")
    private String digioPassword;

    private final RestTemplate restTemplate;

    public KycService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    private KycDetailsRepository kycDetailsRepository;
    @Autowired
    private Authentication authentication;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private HostelService hostelService;

    public ResponseEntity<?> verifyKycStatus() {

        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Utils.UNAUTHORIZED);
        }

        String customerId = authentication.getName();
        Customers customer = customerService.getCustomerInformation(customerId);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.CUSTOMER_NOT_FOUND);
        }

        if (!(CustomerStatus.CHECK_IN.name().equals(customer.getCurrentStatus()) ||
                CustomerStatus.NOTICE.name().equals(customer.getCurrentStatus()) ||
                CustomerStatus.SETTLEMENT_GENERATED.name().equals(customer.getCurrentStatus()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.CUSTOMER_NOT_CHECKED_IN);
        }

        HostelV1 hostel = hostelService.getHostelById(customer.getHostelId());
        if (hostel == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.HOSTEL_NOT_FOUND);
        }

        KycDetails kycDetails = customer.getKycDetails();
        if (kycDetails == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.KYC_DETAILS_NOT_FOUND);
        }

        String digioVerifyUrl = digioUrl + kycDetails.getEntityId() + "/response";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(digioUserName, digioPassword);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>("{}", headers);

            ResponseEntity<DigioKycResponse> response = restTemplate.exchange(
                    digioVerifyUrl,
                    HttpMethod.POST,
                    request,
                    DigioKycResponse.class
            );

            DigioKycResponse digioKycResponse = response.getBody();
            if (digioKycResponse == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No response body found");
            }

            String status = digioKycResponse.status();

            if (response.getStatusCode() == HttpStatus.OK) {
                if (status != null){
                    if (KycStatus.REQUESTED.name().equalsIgnoreCase(status)) {
                        return ResponseEntity.status(HttpStatus.OK).body("Kyc already requested");
                    } else if (KycStatus.VERIFIED.name().equalsIgnoreCase(status)){
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Kyc already verified");
                    } else if (KycStatus.EXPIRED.name().equalsIgnoreCase(status)){
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Kyc request expired");
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Kyc request pending or not available");
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No status found");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request");
            }
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Server error");
        }
    }
}
