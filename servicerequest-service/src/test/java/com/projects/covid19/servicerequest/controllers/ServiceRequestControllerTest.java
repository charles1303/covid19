package com.projects.covid19.servicerequest.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.projects.covid19.servicerequest.components.CustomUserDetails;
import com.projects.covid19.servicerequest.components.JwtManager;
import com.projects.covid19.servicerequest.dto.requests.ServiceRequestDto;
import com.projects.covid19.servicerequest.dto.responses.ApiResponse;
import com.projects.covid19.servicerequest.exceptions.GeneralException;
import com.projects.covid19.servicerequest.models.Role;
import com.projects.covid19.servicerequest.models.ServiceRequest;
import com.projects.covid19.servicerequest.models.User;
import com.projects.covid19.servicerequest.services.ServiceRequestService;
import com.projects.covid19.servicerequest.utils.RoleType;
import com.projects.covid19.servicerequest.utils.StatusTypeEnum;

@ExtendWith(SpringExtension.class)
public class ServiceRequestControllerTest {
	
	@InjectMocks
	private ServiceRequestController serviceRequestController;
	
	@Mock
	private ServiceRequestService serviceRequestService;
	
	@SpyBean
	private JwtManager jwtManager;
	
	@Test
	public void makeRequest_shouldReturnServiceRequestInResponseWhenCalled() throws GeneralException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		
		String token = getAuthenticatedToken("user");
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer "+token);
		
		ServiceRequestDto srequest = new ServiceRequestDto();
		srequest.setItem("Item 1");
		srequest.setQuantity(5);
		srequest.setUnit("cans");
		srequest.setRequestedDate("10-04-2020 19:41");
		srequest.setRequesterId(1L);
		srequest.setAcceptedDate("10-04-2020 19:51");
		srequest.setStatus(StatusTypeEnum.REQUEST);
		
		serviceRequestController.setJwtManager(jwtManager);
		ResponseEntity<ApiResponse> response = (ResponseEntity<ApiResponse>) serviceRequestController.makeRequest(srequest, headers);
		
		assertTrue(response.getStatusCode().equals(HttpStatus.OK));
		assertTrue(response.getBody().getMessage().equalsIgnoreCase("Service request sent successfully"));
		assertNotNull(response.getBody().getResult());
		
	}
	
	private String getAuthenticatedToken(String username) {
		User user = new User();
		user.setUsername(username);
		List<String> myRoles = new ArrayList<>();
		myRoles.add(RoleType.ROLE_ADMIN.getValue());

		Role role = new Role();
		role.setType(RoleType.ROLE_ADMIN);
		user.setRoles(Set.of(role));

		CustomUserDetails userdetails = CustomUserDetails.create(user);

		List<GrantedAuthority> authorities = CustomUserDetails.getAuthorities(myRoles);
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userdetails, "",
				authorities);

		String token = jwtManager.generateToken(authentication);
		return token;
	}


}
