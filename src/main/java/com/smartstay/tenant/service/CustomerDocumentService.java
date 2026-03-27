package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.config.FilesConfig;
import com.smartstay.tenant.config.UploadFileToS3;
import com.smartstay.tenant.dao.CustomerDocuments;
import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.dto.files.UploadFiles;
import com.smartstay.tenant.ennum.DocumentType;
import com.smartstay.tenant.ennum.FileFormat;
import com.smartstay.tenant.ennum.UserType;
import com.smartstay.tenant.payload.customer.CustomerDocumentsIdPayload;
import com.smartstay.tenant.payload.customer.UploadDocuments;
import com.smartstay.tenant.repository.CustomerDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerDocumentService {

    @Autowired
    Authentication authentication;
    @Autowired
    CustomerDocumentRepository customerDocumentRepository;
    @Autowired
    CustomerDuplicateService customerService;
    @Autowired
    UploadFileToS3 uploadFileToS3;

    public List<CustomerDocuments> getDocumentsByCustomerId(String customerId) {
        return customerDocumentRepository
                .findAllByCustomerIdAndIsDeletedFalseAndIsActiveTrueOrderByDocumentIdDesc(customerId);
    }

    public ResponseEntity<?> addCustomerDocuments(List<MultipartFile> listFiles,
                                                  UploadDocuments uploadDocuments) {

        String customerId = authentication.getName();
        Customers customer = customerService.getCustomerById(customerId);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.CUSTOMER_NOT_FOUND);
        }

        List<UploadFiles> uploadLists = listFiles
                .stream()
                .map(i -> uploadFileToS3.uploadCustomerFiles(
                        FilesConfig.convertMultipartToFileNew(i), "customer/additional"))
                .toList();

        if (uploadLists != null && !uploadLists.isEmpty()) {

            List<CustomerDocuments> customerDocuments = uploadLists
                    .stream()
                    .map(i -> {
                        CustomerDocuments cd = new CustomerDocuments();
                        cd.setCustomerId(customerId);
                        cd.setHostelId(customer.getHostelId() != null ? customer.getHostelId() : null);
                        cd.setDocumentUrl(i.fileName());
                        cd.setIsDeleted(false);
                        cd.setIsActive(true);
                        cd.setCreatedBy(customerId);
                        cd.setCreatedAt(new Date());
                        cd.setCreatedByUserType(UserType.TENANT.name());

                        if (i.fileFormat().equalsIgnoreCase("image/png")
                                || i.fileFormat().equalsIgnoreCase("image/jpeg")
                                || i.fileFormat().equalsIgnoreCase("image/jpg")) {
                            cd.setDocumentFileType(FileFormat.IMAGE.name());
                        }
                        else if (i.fileFormat().equalsIgnoreCase("application/pdf")) {
                            cd.setDocumentFileType(FileFormat.PDF.name());
                        }
                        else if (i.fileFormat().equalsIgnoreCase(
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                            cd.setDocumentFileType(FileFormat.DOC.name());
                        }

                        if (uploadDocuments.type().equalsIgnoreCase(DocumentType.KYC.name())) {
                            cd.setDocumentType(DocumentType.KYC.name());
                        }
                        else if (uploadDocuments.type().equalsIgnoreCase(DocumentType.CHECKIN.name())) {
                            cd.setDocumentType(DocumentType.CHECKIN.name());
                        }
                        else {
                            cd.setDocumentType(DocumentType.OTHER.name());
                        }

                        return cd;
                    }).toList();

            customerDocumentRepository.saveAll(customerDocuments);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<?> deleteCustomerDocuments(List<CustomerDocumentsIdPayload> customerDocumentsIdPayloads) {

        String customerId = authentication.getName();
        Customers customer = customerService.getCustomerById(customerId);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Utils.CUSTOMER_NOT_FOUND);
        }

        if (customerDocumentsIdPayloads == null || customerDocumentsIdPayloads.isEmpty()) {
            return new ResponseEntity<>(Utils.NO_DOCUMENT_ID_PROVIDED, HttpStatus.BAD_REQUEST);
        }

        for (CustomerDocumentsIdPayload payload : customerDocumentsIdPayloads) {
            if (payload.documentId() == null) {
                return new ResponseEntity<>(Utils.DOCUMENT_ID_REQUIRED, HttpStatus.BAD_REQUEST);
            }
            if (payload.documentId() <= 0) {
                return new ResponseEntity<>(Utils.DOCUMENT_ID_CANT_BE_ZERO_OR_LESS, HttpStatus.BAD_REQUEST);
            }
        }

        Set<Long> documentIds = customerDocumentsIdPayloads.stream()
                .map(CustomerDocumentsIdPayload::documentId)
                .collect(Collectors.toSet());

        List<CustomerDocuments> customerDocuments = customerDocumentRepository
                .findAllByDocumentIdInAndCustomerIdAndIsDeletedFalseAndIsActiveTrue(documentIds, customerId);

        customerDocuments.forEach(document -> {
            document.setIsDeleted(true);
            document.setIsActive(false);
            document.setUpdatedBy(customerId);
            document.setUpdatedAt(new Date());
        });

        customerDocumentRepository.saveAll(customerDocuments);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
