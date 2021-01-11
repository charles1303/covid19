package com.projects.covid19.servicerequest.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.covid19.servicerequest.components.CustomUserDetails;
import com.projects.covid19.servicerequest.components.JwtManager;
import com.projects.covid19.servicerequest.dto.requests.ServiceRequestDto;
import com.projects.covid19.servicerequest.exceptions.ServiceRequestControllerAdvise;
import com.projects.covid19.servicerequest.interceptors.SecurityInterceptor;
import com.projects.covid19.servicerequest.models.Role;
import com.projects.covid19.servicerequest.models.ServiceRequest;
import com.projects.covid19.servicerequest.models.User;
import com.projects.covid19.servicerequest.services.ServiceRequestService;
import com.projects.covid19.servicerequest.utils.RoleType;
import com.projects.covid19.servicerequest.utils.StatusTypeEnum;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ServiceRequestController.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ServiceRequestControllerAPITest {

	@Autowired
	private MockMvc mvc;

	@InjectMocks
	private ServiceRequestController serviceRequestController;
	
	@SpyBean
	private ServiceRequestControllerAdvise serviceRequestControllerAdvise;

	@MockBean
	private ServiceRequestService serviceRequestService;

	@SpyBean
	private JwtManager jwtManager;

	@Autowired
	static ObjectMapper mapper = new ObjectMapper();

	@SpringBootConfiguration
	@ComponentScan(excludeFilters = @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class))
	public static class config {
	}

	@BeforeAll
	public void setup() {

		SecurityInterceptor interceptor = new SecurityInterceptor();
		serviceRequestController.setServiceRequestService(serviceRequestService);
		serviceRequestController.setJwtManager(jwtManager);
		interceptor.setJwtManager(jwtManager);
		mvc = MockMvcBuilders.standaloneSetup(serviceRequestController)
				.setControllerAdvice(new ServiceRequestControllerAdvise())
				.addInterceptors(interceptor).build();
	}

	@Test
	// @WithAnonymousUser
	public void shouldGetUnauthorizedAndNoHeaderTokenWithAnonymousUser() throws Exception {

		ServiceRequest srequest = new ServiceRequest();
		srequest.setItem("Item 1");
		srequest.setQuantity(5);
		srequest.setUnit("cans");
		srequest.setRequestedDate(LocalDateTime.now());
		srequest.setRequesterId(1L);
		srequest.setAcceptedDate(LocalDateTime.now().plusMinutes(10));
		srequest.setStatus(StatusTypeEnum.REQUEST);

		mvc.perform(post("/api/request/add").with(csrf()).content(asJsonString(srequest))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized())
				.andExpect(header().doesNotExist("token"))
				.andExpect(jsonPath("$.status").value(HttpServletResponse.SC_UNAUTHORIZED))
				.andExpect(jsonPath("$.message").value("Unauthorized user or invalid token"));
	}
	
	@Test
	public void shouldReturnSuccessfulWithWithNewHeaderValidTokenWhenValidTokenIsPassed() throws Exception {

		String token = getAuthenticatedToken();
		
		ServiceRequestDto srequest = new ServiceRequestDto();
		srequest.setItem("Item 1");
		srequest.setQuantity(5);
		srequest.setUnit("cans");
		srequest.setRequestedDate("10-04-2020 19:41");
		srequest.setRequesterId(1L);
		srequest.setAcceptedDate("10-04-2020 19:51");
		srequest.setStatus(StatusTypeEnum.REQUEST);
		
		//{"item":"Item 1","quantity":5,"unit":"cans","requestedDate":"10-04-2020 19:41","requesteeId":null,"requesterId":1,"accepterId":null,"acceptedDate":"10-04-2020 19:51","status":"REQUEST"}
		
		when(serviceRequestService.createServiceRequest(srequest, jwtManager.getUsernameFromJWT(token))).thenReturn(new ServiceRequest());

		mvc.perform(post("/api/request/add").with(csrf()).header("Authorization", "Bearer " + token).content(asJsonString(srequest))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is2xxSuccessful())
				.andExpect(header().exists("token"));
	}
	
	@Test
	public void shouldGetUsernameFromHeader() {
		
		String token = getAuthenticatedToken("user");
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer "+token);
		
		String username = ReflectionTestUtils.invokeMethod(serviceRequestController, "getUsernameFromJwtHeader", headers);
		assertEquals(username, "user");
		
	}

	/*@Test
	// @WithMockUser(roles = "USER")
	public void addCategoryToProvider_shouldReturnServiceProviderInResponseWhenCalled() throws Exception {

		
		String token = getAuthenticatedToken();

		Long providerId = 1L;
		Long categoryId = 1L;

		ServiceProviderCategoryAddRequest spcRequest = new ServiceProviderCategoryAddRequest();
		spcRequest.setProviderId(providerId);
		spcRequest.setCategoryIds(List.of(categoryId));

		ServiceCategory cat = new ServiceCategory();
		cat.setId(categoryId);

		ServiceProvider s = new ServiceProvider();
		s.getCategories().add(cat);

		when(serviceProviderService.addCategoryToProvider(providerId, categoryId)).thenReturn(s);

		ResultActions actions = mvc
				.perform(put("/api/provider/add-cat-to-provider").with(csrf())
						.header("Authorization", "Bearer " + token).content(asJsonString(spcRequest))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.status").value(0))
				.andExpect(jsonPath("$.message").value("Service Provider updated successfully"));

		String response = actions.andReturn().getResponse().getContentAsString();

		ApiResponse apiResponse = mapper.readValue(response, ApiResponse.class);

		assertThat(apiResponse.getMessage()).isEqualTo("Service Provider updated successfully");
		assertThat(apiResponse.getStatus()).isEqualTo(0);

		ServiceProvider spResponse = mapper.convertValue(apiResponse.getResult(), ServiceProvider.class);
		assertTrue(spResponse.getCategories().get(0).getId() == categoryId);

	}*/

	private String getAuthenticatedToken() {
		User user = new User();
		user.setUsername("user");
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


	public static String asJsonString(final Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
