package com.projects.covid19.servicerequest.services;


import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.projects.covid19.servicerequest.dto.requests.ServiceRequestDto;
import com.projects.covid19.servicerequest.models.ServiceRequest;
import com.projects.covid19.servicerequest.repositories.ServiceRequestRepository;
import com.projects.covid19.servicerequest.utils.StatusTypeEnum;

@ExtendWith(SpringExtension.class)
public class ServiceRequestServiceTest {
	
	@InjectMocks
	private ServiceRequestService service;
	
	@Mock
	private ServiceRequestRepository serviceRequestRepository;
	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
	
	@Test
	public void createServiceRequestShouldReturnServiceRequestWhenCalled() {
		
		
		
		ServiceRequest request = new ServiceRequest();
		request.setItem("Item 1");
		request.setQuantity(5);
		request.setUnit("cans");
		request.setRequestedDate(LocalDateTime.now());
		request.setRequesterId(1L);
		request.setAcceptedDate(LocalDateTime.now().plusMinutes(10));
		request.setStatus(StatusTypeEnum.REQUEST);
		
		when(serviceRequestRepository.save(request)).thenReturn(request);
		
		
		
		ServiceRequest requestSaved = service.createServiceRequest(request);
		
		assertEquals(requestSaved, request);
		
	}
	
	@Test
	public void createServiceRequestWithDTOShouldReturnServiceRequestWhenCalled() {
		
		String username = "user";
		
		ServiceRequestDto requestDto = new ServiceRequestDto();
		requestDto.setItem("Item 1");
		requestDto.setQuantity(5);
		requestDto.setUnit("cans");
		requestDto.setRequestedDate("10-04-2020 19:41");
		requestDto.setRequesterId(1L);
		requestDto.setAcceptedDate("10-04-2020 19:51");
		requestDto.setStatus(StatusTypeEnum.REQUEST);
		
		ServiceRequest request = new ServiceRequest();
		request.setItem(requestDto.getItem());
		request.setQuantity(requestDto.getQuantity());
		request.setUnit(requestDto.getUnit());
		request.setRequestedDate(LocalDateTime.parse(requestDto.getRequestedDate(), formatter));
		request.setRequesterId(requestDto.getRequesterId());
		request.setRequesterUsername(username);
		request.setAcceptedDate(LocalDateTime.parse(requestDto.getAcceptedDate(), formatter));
		request.setStatus(requestDto.getStatus());
		
		when(serviceRequestRepository.saveAndFlush(any(ServiceRequest.class))).thenReturn(request);
		
		ServiceRequest requestSaved = service.createServiceRequest(requestDto, username);
		
		assertEquals(requestSaved.getAcceptedDate(), LocalDateTime.parse(requestDto.getAcceptedDate(), formatter));
		assertEquals(requestSaved.getStatus(), requestDto.getStatus());
		
	}

}
