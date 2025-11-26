package com.smartstay.tenant.service;


import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.TransactionV1;
import com.smartstay.tenant.dto.InvoiceItemResponseDTO;
import com.smartstay.tenant.dto.TransactionDto;
import com.smartstay.tenant.mapper.TransactionForCustomerDetailsMapper;
import com.smartstay.tenant.repository.TransactionV1Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionV1Repository transactionV1Repository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private Authentication authentication;


    public ResponseEntity<?> getTransactionList(String hostelId) {
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Utils.UNAUTHORIZED);
        }
        String customerId = authentication.getName();
        if (!customerService.existsByCustomerIdAndHostelId(customerId, hostelId)) {
            return new ResponseEntity<>(Utils.HOSTEL_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        List<TransactionDto> transactionDtos = getTransactionInfoByCustomerId(customerId,hostelId);
        if (transactionDtos.isEmpty()){
            return new ResponseEntity<>(Utils.PAYMENTS_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(transactionDtos, HttpStatus.OK);

    }

    public List<TransactionDto> getTransactionInfoByCustomerId(String customerId, String hostelId) {
        if (authentication.isAuthenticated()) {
            List<TransactionV1> listTransactions = transactionV1Repository.findByCustomerIdAndHostelId(customerId,hostelId);

            return listTransactions.stream()
                    .map(i -> new TransactionForCustomerDetailsMapper().apply(i))
                    .toList();
        }
        return new ArrayList<>();
    }
}
