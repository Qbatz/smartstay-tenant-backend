package com.smartstay.tenant.service;


import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.BillingRules;
import com.smartstay.tenant.dao.BookingsV1;
import com.smartstay.tenant.dao.CustomerCredentials;
import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.dto.hostel.HostelWithRentDTO;
import com.smartstay.tenant.dto.hostel.RentalDetailsDTO;
import com.smartstay.tenant.mapper.hostel.HostelDetailsMapper;
import com.smartstay.tenant.payload.login.*;
import com.smartstay.tenant.repository.CustomerRepository;
import com.smartstay.tenant.repository.HostelRepository;
import com.smartstay.tenant.repository.InvoicesV1Repository;
import com.smartstay.tenant.response.customer.CustomerHostels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LoginService {


    @Autowired
    CustomerRepository customersRepository;
    @Autowired
    Authentication authentication;
    @Autowired
    JWTService jwtService;
    @Autowired
    UserConfigService userConfigService;
    @Autowired
    CustomerCredentialsService customerCredentialsService;

    @Autowired
    BookingsService bookingsService;

    @Autowired
    private HostelRepository hostelRepository;

    @Autowired
    private HostelConfigService hostelConfigService;

    @Autowired
    private InvoicesV1Repository invoicesV1Repository;

    public ResponseEntity<?> updateMpin(UpdateMpin updateMpin) {
        CustomerCredentials credentials = customerCredentialsService.getCustomerCredentialsByXUuid(updateMpin.xuid());
        if (credentials == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer not found.");
        }
        credentials.setPinVerified(true);
        credentials.setCustomerPin(updateMpin.mPin());
        customerCredentialsService.saveCustomerCredentials(credentials);
        List<CustomerHostels> customerHostels = getHostels(credentials.getCustomerMobile());
        return new ResponseEntity<>(customerHostels, HttpStatus.OK);
    }

    public ResponseEntity<?> getHostelsList(String xuid) {
//        if (!authentication.isAuthenticated()){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.UNAUTHORIZED);
//        }
        CustomerCredentials credentials = customerCredentialsService.getCustomerCredentialsByXUuid(xuid);
        if (credentials == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer not found.");
        }
        List<CustomerHostels> customerHostels = getHostels(credentials.getCustomerMobile());
        return new ResponseEntity<>(customerHostels, HttpStatus.OK);
    }

    public ResponseEntity<?> requestToken(RequestToken requestToken) {
        CustomerCredentials credentials = customerCredentialsService.getCustomerCredentialsByXUuid(requestToken.xuid());
        if (credentials == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer not found.");
        }
        Customers customers = customersRepository.findByMobileAndHostelId(credentials.getCustomerMobile(), requestToken.hostelId());
        if (customers == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer not found for the specified hostel. Please register first.");
        }
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("mobile", credentials.getCustomerMobile());
        claims.put("mPin", credentials.getCustomerPin());
        String token = jwtService.generateToken(customers.getCustomerId(), claims);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    public ResponseEntity<?> verifyMpin(VerifyMpin verifyMpin) {
        CustomerCredentials credentials = customerCredentialsService.getCustomerCredentialsByXUuid(verifyMpin.xuid());
        if (credentials == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer not found. Please register first.");
        }
        if (!credentials.getCustomerPin().equals(verifyMpin.mPin())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid M-Pin. Please try again.");
        }
        List<CustomerHostels> customerHostels = getHostels(credentials.getCustomerMobile());
        return new ResponseEntity<>(customerHostels, HttpStatus.OK);
    }

    public ResponseEntity<?> logOut(LogOut logOut) {
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.UNAUTHORIZED);
        }
        CustomerCredentials credentials = customerCredentialsService.getCustomerCredentialsByXUuid(logOut.xuid());
        if (credentials == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer not found.");
        }
        credentials.setFcmToken("");
        customerCredentialsService.saveCustomerCredentials(credentials);
        return new ResponseEntity<>(Utils.UPDATED, HttpStatus.OK);
    }

    public List<CustomerHostels> getHostels(String mobileNo) {
        return hostelRepository.findHostelsByMobile(mobileNo);

    }

    public ResponseEntity<?> getHostelsListWithToken(String xuid) {
        if (!authentication.isAuthenticated()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.UNAUTHORIZED);
        }
        String customerId = authentication.getName();
        List<Customers> customersList = customersRepository.findByXuid(xuid);
        CustomerCredentials credentials = customerCredentialsService.getCustomerCredentialsByXUuid(xuid);
        if (credentials == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer not found.");
        }

        if (customersList == null || customersList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid token. Customer not found.");
        }

        boolean isMatched = customersList.stream()
                .anyMatch(c -> c.getCustomerId().equals(customerId));

        if (!isMatched) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Access denied. XUID does not belong to the authenticated customer.");
        }
        HostelDetailsMapper mapper = new HostelDetailsMapper(bookingsService, hostelConfigService, invoicesV1Repository);
        List<CustomerHostels> customerHostels = getHostels(credentials.getCustomerMobile());

        List<HostelWithRentDTO> mappedList = customerHostels
                .stream()
                .map(mapper)
                .collect(Collectors.toList());
        return new ResponseEntity<>(mappedList, HttpStatus.OK);
    }

    public RentalDetailsDTO getRentDetails(String hostelId, String customerId, BookingsV1 bookingDetails) {

        Double rentAmount = bookingDetails.getRentAmount();
        Double advancePaidAmount = invoicesV1Repository.findAdvancePaidAmount(customerId);
        if (advancePaidAmount == null) {
            advancePaidAmount = 0.0;
        }

        BillingRules billingRules = hostelConfigService.getLatestBillRuleByHostelIdAndStartDate(hostelId, new Date());

        int dueDay = billingRules != null ? billingRules.getBillingDueDate() : 0;
        String dueDateText = (dueDay > 0 ? dueDay : "0") + "th of every month";

        RentalDetailsDTO rentalDetailsDTO = new RentalDetailsDTO();
        rentalDetailsDTO.setJoiningDate(Utils.dateToString(bookingDetails.getJoiningDate()));
        rentalDetailsDTO.setRentAmount(rentAmount);
        rentalDetailsDTO.setAdvancePaidAmount(advancePaidAmount);
        rentalDetailsDTO.setDueDate(dueDateText);

        return rentalDetailsDTO;
    }




}
