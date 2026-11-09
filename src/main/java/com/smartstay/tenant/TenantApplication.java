package com.smartstay.tenant;

import com.smartstay.tenant.dao.AmenityRequest;
import com.smartstay.tenant.ennum.ActionTypeEnum;
import com.smartstay.tenant.repository.AmenityRequestRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

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
}
