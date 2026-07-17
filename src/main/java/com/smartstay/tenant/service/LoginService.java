package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.*;
import com.smartstay.tenant.dto.hostel.HostelWithRentDTO;
import com.smartstay.tenant.dto.hostel.RentalDetailsDTO;
import com.smartstay.tenant.ennum.CustomerBedStatus;
import com.smartstay.tenant.mapper.hostel.HostelDetailsMapper;
import com.smartstay.tenant.payload.login.*;
import com.smartstay.tenant.repository.CustomerRepository;
import com.smartstay.tenant.repository.HostelRepository;
import com.smartstay.tenant.repository.InvoicesV1Repository;
import com.smartstay.tenant.response.customer.CustomerHostelListWrapper;
import com.smartstay.tenant.response.customer.CustomerHostels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
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

        CustomerCredentials credentials = customerCredentialsService
                .getCustomerCredentialsByXUuid(updateMpin.xuid());
        if (credentials == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Customer not found.");
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

        CustomerCredentials credentials = customerCredentialsService
                .getCustomerCredentialsByXUuid(requestToken.xuid());
        if (credentials == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Customer not found.");
        }

        Customers customers = customersRepository
                .findByMobileAndHostelId(credentials.getCustomerMobile(), requestToken.hostelId());
        if (customers == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Customer not found for the specified hostel. Please register first.");
        }

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("mobile", credentials.getCustomerMobile());
        claims.put("mPin", credentials.getCustomerPin());
        String token = jwtService.generateToken(customers.getCustomerId(), claims);

        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    public ResponseEntity<?> verifyMpin(VerifyMpin verifyMpin) {

        CustomerCredentials credentials = customerCredentialsService
                .getCustomerCredentialsByXUuid(verifyMpin.xuid());

        if (credentials == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Customer not found. Please register first.");
        }

        if (!credentials.getCustomerPin().equals(verifyMpin.mPin())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid M-Pin. Please try again.");
        }

        List<CustomerHostels> customerHostels = getHostels(credentials.getCustomerMobile());

        return new ResponseEntity<>(customerHostels, HttpStatus.OK);
    }

    public ResponseEntity<?> logOut(LogOut logOut) {

        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.UNAUTHORIZED);
        }

        CustomerCredentials credentials = customerCredentialsService
                .getCustomerCredentialsByXUuid(logOut.xuid());
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

    public ResponseEntity<?> getHostelsListWithToken(String xuid, String name) {

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

        List<CustomerHostels> customerHostels = getHostels(credentials.getCustomerMobile());

        Set<String> hostelIds = customerHostels.stream()
                .map(CustomerHostels::getHostelId)
                .collect(Collectors.toSet());

        if (name != null) {
            name = !name.isBlank() ? name.trim() : null;
        }

        List<HostelV1> hostels = hostelRepository
                .findAllByHostelIdInAndHostelNameContainingIgnoreCaseAndIsActiveTrueAndIsDeletedFalse(hostelIds, name);

        Map<String, HostelV1> hostelMap = hostels.stream()
                .collect(Collectors.toMap(HostelV1::getHostelId,
                        Function.identity()));

        Map<String, Customers> customerMap = customersList.stream()
                .collect(Collectors.toMap(Customers::getCustomerId,
                        Function.identity()));

        List<HostelWithRentDTO> activeStays = new ArrayList<>();
        List<HostelWithRentDTO> previousStays = new ArrayList<>();

        for (CustomerHostels customerHostel : customerHostels) {

            Customers customer = customerMap.getOrDefault(customerHostel.getCustomerId(), null);
            HostelV1 hostel = hostelMap.getOrDefault(customerHostel.getHostelId(), null);

            if (customer == null || hostel == null) {
                continue;
            }

            HostelDetailsMapper mapper = new HostelDetailsMapper(bookingsService,
                    hostelConfigService, invoicesV1Repository, hostel, customer);

            if (CustomerBedStatus.BED_ASSIGNED.name().equals(customer.getCustomerBedStatus())){
                activeStays.add(mapper.apply(customerHostel));
            } else {
                previousStays.add(mapper.apply(customerHostel));
            }
        }

        CustomerHostelListWrapper response = new CustomerHostelListWrapper(activeStays, previousStays);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public RentalDetailsDTO getRentDetails(String hostelId, String customerId, BookingsV1 bookingDetails) {

        Double rentAmount = bookingDetails.getRentAmount();
        Double advancePaidAmount = invoicesV1Repository.findAdvancePaidAmount(customerId);
        if (advancePaidAmount == null) {
            advancePaidAmount = 0.0;
        }

        BillingRules billingRules = hostelConfigService.getLatestBillRuleByHostelIdAndStartDate(hostelId, new Date());

        String dueDateText =  " 1st of every month";
        if (billingRules != null) {
            if (billingRules.getBillingStartDate() == 1) {
                dueDateText =billingRules.getBillingStartDate()+ "st of every month";
            }
            else if (billingRules.getBillingStartDate() == 2){
                dueDateText =billingRules.getBillingStartDate()+ "nd of every month";
            }
            else if (billingRules.getBillingStartDate() == 3){
                dueDateText =billingRules.getBillingStartDate()+ "rd of every month";
            }else {
                dueDateText =billingRules.getBillingStartDate()+ "th of every month";
            }

        }

        RentalDetailsDTO rentalDetailsDTO = new RentalDetailsDTO();
        rentalDetailsDTO.setJoiningDate(Utils.dateToString(bookingDetails.getJoiningDate()));
        rentalDetailsDTO.setRentAmount(rentAmount);
        rentalDetailsDTO.setAdvancePaidAmount(advancePaidAmount);
        rentalDetailsDTO.setDueDate(dueDateText);

        return rentalDetailsDTO;
    }
}
