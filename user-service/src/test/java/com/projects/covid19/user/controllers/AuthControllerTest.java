package com.projects.covid19.user.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.covid19.user.dto.requests.SignUpRequest;
import com.projects.covid19.user.dto.responses.ApiResponse;
import com.projects.covid19.user.models.User;
import com.projects.covid19.user.services.UserService;
import com.projects.covid19.user.utils.RoleType;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties", properties = "spring.autoconfigure.exclude=com.projects.covid19.user.config.JpaConfig")
public class AuthControllerTest {
	
	@InjectMocks
    private AuthController authController;
	
	
	@Mock
    private UserService userService;
	
	@Test
	public void registerUser_shouldRetunOkResponse_whenCalledWithSignUpRequest() throws Exception {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		
		SignUpRequest signUpRequest = new SignUpRequest();
		signUpRequest.setEmail("user@emai.com");
		signUpRequest.setName("User1");
		signUpRequest.setUsername("user@emai.com");
		signUpRequest.setPassword("xxxxxx");
		
		when(userService.registerUser(any(SignUpRequest.class), any(RoleType.class))).thenReturn(new User());
		
		ResponseEntity<ApiResponse> response = (ResponseEntity<ApiResponse>) authController.registerUser(signUpRequest);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getMessage()).isEqualTo("User registered successfully");
		
	}
	
	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
