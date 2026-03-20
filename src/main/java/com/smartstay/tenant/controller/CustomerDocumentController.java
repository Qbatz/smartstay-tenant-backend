package com.smartstay.tenant.controller;

import com.smartstay.tenant.payload.customer.CustomerDocumentsIdPayload;
import com.smartstay.tenant.payload.customer.UploadDocuments;
import com.smartstay.tenant.service.CustomerDocumentService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("v2/customer-document")
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@SecurityRequirement(name = "Authorization")
@CrossOrigin("*")
public class CustomerDocumentController {

    @Autowired
    CustomerDocumentService customerDocumentService;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addCustomerDocuments(@RequestPart(name = "files") List<MultipartFile> listFiles,
                                                  @Valid @RequestPart(name = "payload") UploadDocuments uploadDocuments) {
        return customerDocumentService.addCustomerDocuments(listFiles, uploadDocuments);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCustomerDocuments(@RequestBody List<CustomerDocumentsIdPayload> customerDocumentsIdPayloads){
        return customerDocumentService.deleteCustomerDocuments(customerDocumentsIdPayloads);
    }
}
