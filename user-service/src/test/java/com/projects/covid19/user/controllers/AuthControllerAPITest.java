package com.projects.covid19.user.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.covid19.user.dto.requests.SignUpRequest;
import com.projects.covid19.user.dto.responses.ApiResponse;
import com.projects.covid19.user.models.User;
import com.projects.covid19.user.services.CustomUserDetailsService;
import com.projects.covid19.user.services.RoleService;
import com.projects.covid19.user.services.UserService;
import com.projects.covid19.user.utils.RoleType;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AuthController.class)
public class AuthControllerAPITest {
	
	@Autowired
	private MockMvc mvc;

	@MockBean
	private UserService userService;
	
	@MockBean
	private RoleService roleService;
	
	@MockBean
	private CustomUserDetailsService customUserDetailsService;
	
	@Test
	public void registerUser_shouldRetunOkResponse_whenCalledWithSignUpRequest() throws Exception {
		SignUpRequest signUpRequest = new SignUpRequest();
		signUpRequest.setEmail("user@emai.com");
		signUpRequest.setName("User1");
		signUpRequest.setUsername("user@emai.com");
		signUpRequest.setPassword("xxxxxxxxxx");
		
		
		
		when(userService.registerUser(any(SignUpRequest.class), any(RoleType.class))).thenReturn(new User());
		
		ResultActions actions = mvc.perform(post("/api/auth/register").content(asJsonString(signUpRequest)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.status").value(0))
				.andExpect(jsonPath("$.message").value("User registered successfully"));
		
		String response = actions.andReturn().getResponse().getContentAsString();
		
		ApiResponse apiResponse = new ObjectMapper().readValue(response, ApiResponse.class);
		
		assertThat(apiResponse.getMessage()).isEqualTo("User registered successfully");
		assertThat(apiResponse.getStatus()).isEqualTo(0);
		
		
	}
	
	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
