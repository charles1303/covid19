package com.projects.covid19.deliveryrequest.controllers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.projects.covid19.deliveryrequest.dto.requests.DeliveryRequestDto;
import com.projects.covid19.deliveryrequest.dto.responses.ApiResponse;
import com.projects.covid19.deliveryrequest.exceptions.GeneralException;
import com.projects.covid19.deliveryrequest.models.DeliveryRequest;
import com.projects.covid19.deliveryrequest.services.DeliveryRequestService;
import com.projects.covid19.deliveryrequest.utils.StatusTypeEnum;

@ExtendWith(SpringExtension.class)
public class DeliveryRequestControllerTest {
	
	@InjectMocks
	private DeliveryRequestController deliveryRequestController;
	
	@Mock
	private DeliveryRequestService deliveryRequestService;
	
	@Test
	public void makeRequest_shouldReturnServiceRequestInResponseWhenCalled() throws GeneralException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		
		DeliveryRequestDto srequest = new DeliveryRequestDto();
		srequest.setServiceRequestId(1L);
		srequest.setItem("Item 1");
		srequest.setQuantity(5);
		srequest.setUnit("cans");
		srequest.setRequestedDate("10-04-2020 19:41");
		srequest.setRequesterId(1L);
		srequest.setAcceptedDate("10-04-2020 19:51");
		srequest.setName("Cust 1");
		srequest.setPhoneNumber("08091234567");
		srequest.setAddress("Address 1");
		srequest.setStatus(StatusTypeEnum.REQUEST);
		
		when(deliveryRequestService.createDeliveryRequest(srequest)).thenReturn(new DeliveryRequest());
		
		ResponseEntity<ApiResponse> response = (ResponseEntity<ApiResponse>) deliveryRequestController.makeRequest(srequest);
		
		assertTrue(response.getStatusCode().equals(HttpStatus.OK));
		assertTrue(response.getBody().getMessage().equalsIgnoreCase("Delivery request sent successfully"));
		assertNotNull(response.getBody().getResult());
		
	}

}
