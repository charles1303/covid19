package com.projects.covid19.deliveryrequest.repo;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.projects.covid19.deliveryrequest.dto.requests.DeliveryRequestDto;
import com.projects.covid19.deliveryrequest.models.DeliveryRequest;
import com.projects.covid19.deliveryrequest.repositories.DeliveryRequestRepository;
import com.projects.covid19.deliveryrequest.utils.StatusTypeEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class DeliveryRequestRepoTest {
	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

	
	@Autowired
	private DeliveryRequestRepository deliveryRequestRepository;
	
	@DisplayName("Service Request when saved should return saved Service Request")
	@Test
	public void whenServiceRequestIsSaved_thenReturnServiceRequest() {
		
		DeliveryRequestDto requestDto = new DeliveryRequestDto();
		requestDto.setServiceRequestId(1L);
		requestDto.setItem("Item 1");
		requestDto.setQuantity(5);
		requestDto.setUnit("cans");
		requestDto.setRequestedDate("10-04-2020 19:41");
		requestDto.setRequesterId(1L);
		requestDto.setAcceptedDate("10-04-2020 19:51");
		requestDto.setName("Cust 1");
		requestDto.setPhoneNumber("08091234567");
		requestDto.setAddress("Address 1");
		requestDto.setStatus(StatusTypeEnum.REQUEST);
		
		DeliveryRequest request = new DeliveryRequest();
		request.setServiceRequestId(requestDto.getServiceRequestId());
		request.setItem(requestDto.getItem());
		request.setQuantity(requestDto.getQuantity());
		request.setUnit(requestDto.getUnit());
		
		request.setAcceptedDate(LocalDateTime.parse(requestDto.getAcceptedDate(), formatter));
		request.setName(requestDto.getName());
		request.setPhoneNumber(requestDto.getPhoneNumber());
		request.setAddress(requestDto.getAddress());
		
		request.setStatus(requestDto.getStatus());
		
		assertNull(request.getId());
		
		DeliveryRequest requestSaved = deliveryRequestRepository.save(request);
		
		assertNotNull(requestSaved.getId());
		
		
		
		log.error(request.getAcceptedDate().toString());
		
		
	}
	
	
}
