package com.projects.covid19.serviceprovider.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

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

import com.projects.covid19.serviceprovider.dto.requests.ServiceProviderCategoryAddRequest;
import com.projects.covid19.serviceprovider.dto.responses.ApiResponse;
import com.projects.covid19.serviceprovider.exceptions.GeneralException;
import com.projects.covid19.serviceprovider.models.ServiceProvider;
import com.projects.covid19.serviceprovider.services.ServiceProviderService;

@ExtendWith(SpringExtension.class)
public class ServiceProviderControllerTest {
	
	@InjectMocks
	private ServiceProviderController serviceProviderController;
	
	@Mock
	private ServiceProviderService serviceProviderService;
	
	@Test
	public void addCategoryToProvider_shouldReturnServiceProviderInResponseWhenCalled() throws GeneralException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		
		Long providerId = 1L;
		Long categoryId = 1L;
		
		ServiceProviderCategoryAddRequest spcRequest = new ServiceProviderCategoryAddRequest();
		spcRequest.setProviderId(providerId);
		spcRequest.setCategoryIds(List.of(categoryId));
		
		when(serviceProviderService.addCategoryToProvider(providerId, categoryId)).thenReturn(new ServiceProvider());
		
		ResponseEntity<ApiResponse> response = (ResponseEntity<ApiResponse>) serviceProviderController.addCategoryToProvider(spcRequest);
		
		assertTrue(response.getStatusCode().equals(HttpStatus.OK));
		assertTrue(response.getBody().getMessage().equalsIgnoreCase("Service Provider updated successfully"));
		
	}
	
	

}
