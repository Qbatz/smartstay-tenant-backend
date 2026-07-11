package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.dao.HostelV1;
import com.smartstay.tenant.dao.KycDetails;
import com.smartstay.tenant.dto.kyc.DigioInitiateKycRequest;
import com.smartstay.tenant.dto.kyc.DigioInitiateKycResponse;
import com.smartstay.tenant.dto.kyc.DigioKycResponse;
import com.smartstay.tenant.ennum.CustomerStatus;
import com.smartstay.tenant.ennum.KycStatus;
import com.smartstay.tenant.repository.KycDetailsRepository;
import com.smartstay.tenant.response.kyc.NotificationKycInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
public class KycService {

    @Value("${DIGIO_URL}")
    private String digioUrl;
    @Value("${DIGIO_REQUEST_WITH_TEMPLATE_URL}")
    private String digioRequestWithTemplateUrl;
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
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.RESPONSE_BODY_NOT_FOUND);
            }

            String status = digioKycResponse.status();

            if (response.getStatusCode() == HttpStatus.OK) {
                if (status != null){
                    if (KycStatus.REQUESTED.name().equalsIgnoreCase(status)) {
                        return ResponseEntity.status(HttpStatus.OK).body(Utils.KYC_ALREADY_REQUESTED);
                    } else if (KycStatus.VERIFIED.name().equalsIgnoreCase(status)){
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.KYC_ALREADY_VERIFIED);
                    } else if (KycStatus.EXPIRED.name().equalsIgnoreCase(status)){
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.KYC_REQUEST_EXPIRED);
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.KYC_REQUEST_PENDING_OR_NOT_AVAILABLE);
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.STATUS_NOT_FOUND);
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.INVALID_REQUEST);
            }
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.SERVER_ERROR);
        }
    }

    public ResponseEntity<?> updateStatusToWaitingApproval() {

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

        String currentStatus = kycDetails.getCurrentStatus();

        if (!KycStatus.REQUESTED.name().equalsIgnoreCase(currentStatus)){
            String errorMessage = Utils.KYC_STATUS_MUST_BE_REQUESTED;

            if (KycStatus.PENDING.name().equalsIgnoreCase(currentStatus)){
                errorMessage = Utils.KYC_STATUS_CAN_NOT_BE_PENDING;
            } else if (KycStatus.VERIFIED.name().equalsIgnoreCase(currentStatus)) {
                errorMessage = Utils.KYC_STATUS_CAN_NOT_BE_VERIFIED;
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }

        Date today = new Date();

        kycDetails.setCurrentStatus(KycStatus.WAITING_FOR_APPROVAL.name());
        kycDetails.setUpdatedAt(today);

        kycDetailsRepository.save(kycDetails);

        return ResponseEntity.status(HttpStatus.OK).body(Utils.SUCCESS);
    }

    public ResponseEntity<?> initiateKyc() {

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
        if (kycDetails != null) {
            if (kycDetails.getCurrentStatus().equalsIgnoreCase(KycStatus.VERIFIED.name())) {
                return new ResponseEntity<>(Utils.CUSTOMER_VERIFIED_KYC, HttpStatus.BAD_REQUEST);
            }
            if (kycDetails.getCurrentStatus().equalsIgnoreCase(KycStatus.WAITING_FOR_APPROVAL.name())) {
                return new ResponseEntity<>(Utils.KYC_VERIFICATION_ALREADY_REQUESTED, HttpStatus.BAD_REQUEST);
            }
        }

        String digioInitiateUrl = digioRequestWithTemplateUrl;

        Date today = new Date();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(digioUserName, digioPassword);
            headers.setContentType(MediaType.APPLICATION_JSON);

            DigioInitiateKycRequest initiateKycRequest = new DigioInitiateKycRequest(customer.getMobile(),
                    true, "SMS", Utils.getFullName(customer.getFirstName(),
                    customer.getLastName()), "SMARTSTAY-WORKFLOW", true);

            HttpEntity<DigioInitiateKycRequest> request = new HttpEntity<>(initiateKycRequest, headers);

            ResponseEntity<DigioInitiateKycResponse> response = restTemplate.exchange(
                    digioInitiateUrl,
                    HttpMethod.POST,
                    request,
                    DigioInitiateKycResponse.class
            );

            DigioInitiateKycResponse digioInitiateKycResponse = response.getBody();
            if (digioInitiateKycResponse == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.RESPONSE_BODY_NOT_FOUND);
            }

            if (response.getStatusCode() == HttpStatus.OK) {

                DigioInitiateKycResponse.AccessToken kycAccessToken = digioInitiateKycResponse.accessToken();
                if (kycDetails == null) {
                    kycDetails = new KycDetails();
                    kycDetails.setCustomers(customer);
                    kycDetails.setCreatedAt(today);
                    kycDetails.setCreatedBy(authentication.getName());
                }
                kycDetails.setCurrentStatus(KycStatus.REQUESTED.name());
                kycDetails.setTransactionId(digioInitiateKycResponse.transactionId());
                kycDetails.setTemplateId(digioInitiateKycResponse.templateId());
                kycDetails.setReferenceId(digioInitiateKycResponse.referenceId());

                if (kycAccessToken != null) {
                    kycDetails.setEntityId(kycAccessToken.entityId());
                    kycDetails.setAccessTokenId(kycAccessToken.id());
                    kycDetails.setExpireAt(Utils.stringDateToDate(kycAccessToken.validTill()));
                }

                kycDetails = kycDetailsRepository.save(kycDetails);

                NotificationKycInfo notificationKycInfo = new NotificationKycInfo(
                        kycDetails.getEntityId(), kycDetails.getAccessTokenId(), customer.getMobile()
                );

                return ResponseEntity.status(HttpStatus.OK).body(notificationKycInfo);
            }  else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.INVALID_REQUEST);
            }
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.SERVER_ERROR);
        }
    }
}
