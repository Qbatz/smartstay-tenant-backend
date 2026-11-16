package com.smartstay.tenant;

import com.smartstay.tenant.dao.*;
import com.smartstay.tenant.ennum.*;
import com.smartstay.tenant.repository.*;
import com.smartstay.tenant.response.hostel.Complaints;
import com.smartstay.tenant.service.CustomerActivitiesService;
import com.smartstay.tenant.service.CustomerService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
@EnableScheduling
@SpringBootApplication
public class TenantApplication {

	public static void main(String[] args) {
		SpringApplication.run(TenantApplication.class, args);
	}

//	@Bean
//	CommandLineRunner setActivationActionTypeForAmenityRequests(AmenityRequestRepository amenityRequestRepository) {
//		return (args) -> {
//
//			List<AmenityRequest> amenityRequests = amenityRequestRepository.findAll();
//
//			for (AmenityRequest amenityRequest : amenityRequests) {
//				if (amenityRequest.getActionType() == null){
//					amenityRequest.setActionType(ActionTypeEnum.ACTIVATION.name());
//				}
//			}
//
//			amenityRequestRepository.saveAll(amenityRequests);
//		};
//	}

//	@Bean
//	CommandLineRunner setOldDataToCustomerActivities(CustomerActivitiesRepository customerActivitiesRepository,
//													 AmenityRequestRepository amenityRequestRepository,
//													 CustomerActivitiesService customerActivitiesService,
//													 CustomerService customerService,
//													 BedChangeRequestRepo bedChangeRequestRepository,
//													 ComplaintsV1Repository complaintsV1Repository,
//													 ComplaintCommentsRepository complaintCommentsRepository,
//													 CustomerAdditionalContactsRepository customerAdditionalContactsRepository,
//													 RaiseNoticeRequestRepository raiseNoticeRequestRepository,
//													 CustomerDocumentRepository customerDocumentRepository,
//													 CustomerJobDetailsRepository customerJobDetailsRepository) {
//
//		return (args) -> {
//
//			List<CustomerActivities> customerActivities = customerActivitiesRepository.findAll();
//
//			Set<String> existingActivities = customerActivities.stream()
//					.filter(i -> i.getSource() != null && i.getSourceId() != null)
//					.map(i -> i.getSource() + "_" + i.getSourceId())
//					.collect(Collectors.toSet());
//
//			// amenity requests
//			List<AmenityRequest> amenityRequests = amenityRequestRepository.findAll();
//
//			for (AmenityRequest amenityRequest : amenityRequests) {
//
//				String sourceId = String.valueOf(amenityRequest.getAmenityRequestId());
//				String key = CustomerActivitySourceEnum.AMENITY_REQUEST.name() + "_" + sourceId;
//
//				if (existingActivities.contains(key)) {
//					continue;
//				}
//
//				String tenantPlatform = amenityRequest.getTenantPlatform();
//				String customerId = amenityRequest.getCustomerId();
//				Date createdAt = amenityRequest.getCreatedAt();
//
//				if (tenantPlatform == null){
//					tenantPlatform = TenantPlatformEnum.ANDROID.name();
//				}
//				Customers customer = customerService.getCustomerInformation(customerId);
//				if (customer == null) {
//					continue;
//				}
//
//				customerActivitiesService.createCustomerActivity(customer, tenantPlatform,
//						CustomerActivityTypeEnum.CREATE, CustomerActivitySourceEnum.AMENITY_REQUEST,
//						String.valueOf(amenityRequest.getAmenityRequestId()), createdAt,
//						UserType.ALL_EXCEPT_TENANT.name(), null);
//
//				existingActivities.add(key);
//			}
//
//			// bed change requests
//			List<BedChangeRequest> bedChangeRequests = bedChangeRequestRepository.findAll();
//
//			for (BedChangeRequest bedChangeRequest : bedChangeRequests) {
//
//				String sourceId = String.valueOf(bedChangeRequest.getId());
//				String key = CustomerActivitySourceEnum.BED_CHANGE_REQUEST.name() + "_" + sourceId;
//
//				if (existingActivities.contains(key)) {
//					continue;
//				}
//
//				String tenantPlatform = bedChangeRequest.getTenantPlatform();
//				String customerId = bedChangeRequest.getCustomerId();
//				Date createdAt = bedChangeRequest.getCreatedAt();
//
//				if (tenantPlatform == null){
//					tenantPlatform = TenantPlatformEnum.ANDROID.name();
//				}
//				Customers customer = customerService.getCustomerInformation(customerId);
//				if (customer == null) {
//					continue;
//				}
//
//				customerActivitiesService.createCustomerActivity(customer, tenantPlatform,
//						CustomerActivityTypeEnum.CREATE, CustomerActivitySourceEnum.BED_CHANGE_REQUEST,
//						String.valueOf(bedChangeRequest.getId()), createdAt, UserType.ALL_EXCEPT_TENANT.name(),
//						null);
//
//				existingActivities.add(key);
//			}
//
//			// complaints
//			List<ComplaintsV1> complaints = complaintsV1Repository.findAll();
//
//			for (ComplaintsV1 complaint : complaints) {
//
//				String sourceId = String.valueOf(complaint.getComplaintId());
//				String key = CustomerActivitySourceEnum.COMPLAINT.name() + "_" + sourceId;
//
//				if (existingActivities.contains(key)) {
//					continue;
//				}
//
//				String tenantPlatform = complaint.getTenantPlatform();
//				String customerId = complaint.getCustomerId();
//				Date createdAt = complaint.getCreatedAt();
//
//				if (customerId == null || (!Objects.equals(customerId, complaint.getCreatedBy()))) {
//					continue;
//				}
//
//				if (tenantPlatform == null){
//					tenantPlatform = TenantPlatformEnum.ANDROID.name();
//				}
//				Customers customer = customerService.getCustomerInformation(customerId);
//				if (customer == null) {
//					continue;
//				}
//
//				customerActivitiesService.createCustomerActivity(customer, tenantPlatform,
//						CustomerActivityTypeEnum.CREATE, CustomerActivitySourceEnum.COMPLAINT,
//						String.valueOf(complaint.getComplaintId()), createdAt, UserType.ALL_EXCEPT_TENANT.name(),
//						null);
//
//				existingActivities.add(key);
//			}
//
//			// complaint comments
//			List<ComplaintComments> complaintComments = complaintCommentsRepository.findAll();
//
//			for (ComplaintComments complaintComment : complaintComments) {
//
//				String sourceId = String.valueOf(complaintComment.getCommentId());
//				String key = CustomerActivitySourceEnum.COMPLAINT_COMMENTS.name() + "_" + sourceId;
//
//				if (existingActivities.contains(key)) {
//					continue;
//				}
//
//				String tenantPlatform = complaintComment.getTenantPlatform();
//				String customerId = complaintComment.getComplaint() != null ?
//						complaintComment.getComplaint().getCustomerId() : null;
//				Date createdAt = complaintComment.getCreatedAt();
//
//				if (customerId == null || (!Objects.equals(customerId, complaintComment.getCreatedBy())
//						&& !UserType.TENANT.name().equalsIgnoreCase(complaintComment.getUserType()))) {
//					continue;
//				}
//
//				if (tenantPlatform == null){
//					tenantPlatform = TenantPlatformEnum.ANDROID.name();
//				}
//				Customers customer = customerService.getCustomerInformation(customerId);
//				if (customer == null) {
//					continue;
//				}
//
//				customerActivitiesService.createCustomerActivity(customer, tenantPlatform,
//						CustomerActivityTypeEnum.CREATE, CustomerActivitySourceEnum.COMPLAINT_COMMENTS,
//						String.valueOf(complaintComment.getCommentId()), createdAt, UserType.ALL_EXCEPT_TENANT.name(),
//						null);
//
//				existingActivities.add(key);
//			}
//
//			// customer additional contacts
//			List<CustomerAdditionalContacts> customerAdditionalContacts = customerAdditionalContactsRepository.findAll();
//
//			for (CustomerAdditionalContacts customerAdditionalContact : customerAdditionalContacts) {
//
//				String sourceId = String.valueOf(customerAdditionalContact.getContactId());
//				String key = CustomerActivitySourceEnum.CUSTOMER_ADDITIONAL_CONTACTS.name() + "_" + sourceId;
//
//				if (existingActivities.contains(key)) {
//					continue;
//				}
//
//				String tenantPlatform = customerAdditionalContact.getTenantPlatform();
//				String customerId = customerAdditionalContact.getCustomerId();
//				Date createdAt = customerAdditionalContact.getCreatedAt();
//
//				if (customerId == null || (!Objects.equals(customerId, customerAdditionalContact.getCreatedBy())
//						&& !UserType.TENANT.name().equalsIgnoreCase(customerAdditionalContact.getAddedByUserType()))){
//					continue;
//				}
//
//				if (tenantPlatform == null){
//					tenantPlatform = TenantPlatformEnum.ANDROID.name();
//				}
//				Customers customer = customerService.getCustomerInformation(customerId);
//				if (customer == null) {
//					continue;
//				}
//
//				customerActivitiesService.createCustomerActivity(customer, tenantPlatform,
//						CustomerActivityTypeEnum.CREATE, CustomerActivitySourceEnum.CUSTOMER_ADDITIONAL_CONTACTS,
//						String.valueOf(customerAdditionalContact.getContactId()), createdAt, UserType.ALL_EXCEPT_TENANT.name(),
//						null);
//
//				existingActivities.add(key);
//			}
//
//			// raise notice request
//			List<RaiseNoticeRequest> raiseNoticeRequests = raiseNoticeRequestRepository.findAll();
//
//			for (RaiseNoticeRequest raiseNoticeRequest : raiseNoticeRequests) {
//
//				String sourceId = String.valueOf(raiseNoticeRequest.getId());
//				String key = CustomerActivitySourceEnum.RAISE_NOTICE_REQUEST.name() + "_" + sourceId;
//
//				if (existingActivities.contains(key)) {
//					continue;
//				}
//
//				String tenantPlatform = raiseNoticeRequest.getTenantPlatform();
//				String customerId = raiseNoticeRequest.getCustomerId();
//				Date createdAt = raiseNoticeRequest.getCreatedAt();
//
//				if (tenantPlatform == null){
//					tenantPlatform = TenantPlatformEnum.ANDROID.name();
//				}
//				Customers customer = customerService.getCustomerInformation(customerId);
//				if (customer == null) {
//					continue;
//				}
//
//				customerActivitiesService.createCustomerActivity(customer, tenantPlatform,
//						CustomerActivityTypeEnum.CREATE, CustomerActivitySourceEnum.RAISE_NOTICE_REQUEST,
//						String.valueOf(raiseNoticeRequest.getId()), createdAt, UserType.ALL_EXCEPT_TENANT.name(),
//						null);
//
//				existingActivities.add(key);
//			}
//
//			// customer documents
//			List<CustomerDocuments> customerDocuments = customerDocumentRepository.findAll();
//
//			for (CustomerDocuments customerDocument: customerDocuments) {
//
//				String sourceId = String.valueOf(customerDocument.getDocumentId());
//				String key = CustomerActivitySourceEnum.CUSTOMER_DOCUMENTS.name() + "_" + sourceId;
//
//				if (existingActivities.contains(key)) {
//					continue;
//				}
//
//				String tenantPlatform = customerDocument.getTenantPlatform();
//				String customerId = customerDocument.getCustomerId();
//				Date createdAt = customerDocument.getCreatedAt();
//
//				if (customerId == null || (!Objects.equals(customerId, customerDocument.getCreatedBy())
//						&& !UserType.TENANT.name().equalsIgnoreCase(customerDocument.getCreatedByUserType()))){
//					continue;
//				}
//
//				if (tenantPlatform == null){
//					tenantPlatform = TenantPlatformEnum.ANDROID.name();
//				}
//				Customers customer = customerService.getCustomerInformation(customerId);
//				if (customer == null) {
//					continue;
//				}
//
//				customerActivitiesService.createCustomerActivity(customer, tenantPlatform,
//						CustomerActivityTypeEnum.CREATE, CustomerActivitySourceEnum.CUSTOMER_DOCUMENTS,
//						String.valueOf(customerDocument.getDocumentId()), createdAt, UserType.ALL_EXCEPT_TENANT.name(),
//						null);
//
//				existingActivities.add(key);
//			}
//
//			// customer job details
//			List<CustomerJobDetails> customerJobDetails = customerJobDetailsRepository.findAll();
//
//			for (CustomerJobDetails customerJobDetail: customerJobDetails) {
//
//				String sourceId = String.valueOf(customerJobDetail.getJobId());
//				String key = CustomerActivitySourceEnum.CUSTOMER_JOB_DETAILS.name() + "_" + sourceId;
//
//				if (existingActivities.contains(key)) {
//					continue;
//				}
//
//				String tenantPlatform = customerJobDetail.getTenantPlatform();
//				String customerId = customerJobDetail.getCustomerId();
//				Date createdAt = customerJobDetail.getCreatedAt();
//
//				if (customerId == null || (!Objects.equals(customerId, customerJobDetail.getCreatedBy())
//					&& !UserType.TENANT.name().equalsIgnoreCase(customerJobDetail.getCreatedByUserType()))){
//					continue;
//				}
//
//				if (tenantPlatform == null){
//					tenantPlatform = TenantPlatformEnum.ANDROID.name();
//				}
//				Customers customer = customerService.getCustomerInformation(customerId);
//				if (customer == null) {
//					continue;
//				}
//
//				customerActivitiesService.createCustomerActivity(customer, tenantPlatform,
//						CustomerActivityTypeEnum.CREATE, CustomerActivitySourceEnum.CUSTOMER_JOB_DETAILS,
//						String.valueOf(customerJobDetail.getJobId()), createdAt, UserType.ALL_EXCEPT_TENANT.name(),
//						null);
//
//				existingActivities.add(key);
//			}
//		};
//	}
}
