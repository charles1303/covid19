package com.projects.covid19.deliveryrequest.services;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.projects.covid19.deliveryrequest.dto.requests.DeliveryRequestDto;
import com.projects.covid19.deliveryrequest.models.DeliveryRequest;
import com.projects.covid19.deliveryrequest.repositories.DeliveryRequestRepository;
import com.projects.covid19.deliveryrequest.utils.StatusTypeEnum;

@ExtendWith(SpringExtension.class)
public class DeliveryRequestServiceTest {
	
	@InjectMocks
	private DeliveryRequestService service;
	
	@Mock
	private DeliveryRequestRepository deliveryRequestRepository;
	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
	
	@Test
	public void createServiceRequestShouldReturnServiceRequestWhenCalled() {
		
		DeliveryRequest request = new DeliveryRequest();
		request.setServiceRequestId(1L);
		request.setItem("Item 1");
		request.setQuantity(5);
		request.setUnit("cans");
		request.setAcceptedDate(LocalDateTime.now().plusMinutes(10));
		request.setStatus(StatusTypeEnum.REQUEST);
		request.setName("Cust 1");
		request.setPhoneNumber("08091234567");
		request.setAddress("Address 1");
		
		when(deliveryRequestRepository.save(request)).thenReturn(request);
		
		
		
		DeliveryRequest requestSaved = service.createDeliveryRequest(request);
		
		assertEquals(requestSaved, request);
		
	}
	
	@Test
	public void createServiceRequestWithDTOShouldReturnServiceRequestWhenCalled() {
		
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
		request.setItem(requestDto.getItem());
		request.setQuantity(requestDto.getQuantity());
		request.setUnit(requestDto.getUnit());
		request.setAcceptedDate(LocalDateTime.parse(requestDto.getAcceptedDate(), formatter));
		request.setName(requestDto.getName());
		request.setPhoneNumber(requestDto.getPhoneNumber());
		request.setAddress(requestDto.getAddress());
		request.setStatus(requestDto.getStatus());;
		
		when(deliveryRequestRepository.save(any(DeliveryRequest.class))).thenReturn(request);
		
		DeliveryRequest requestSaved = service.createDeliveryRequest(requestDto);
		
		assertEquals(requestSaved.getAcceptedDate(), LocalDateTime.parse(requestDto.getAcceptedDate(), formatter));
		
	}

}
