package com.projects.covid19.deliveryrequest.controllers;

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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.covid19.deliveryrequest.components.CustomUserDetails;
import com.projects.covid19.deliveryrequest.components.JwtManager;
import com.projects.covid19.deliveryrequest.dto.requests.DeliveryRequestDto;
import com.projects.covid19.deliveryrequest.exceptions.DeliveryRequestControllerAdvise;
import com.projects.covid19.deliveryrequest.interceptors.SecurityInterceptor;
import com.projects.covid19.deliveryrequest.models.DeliveryRequest;
import com.projects.covid19.deliveryrequest.models.Role;
import com.projects.covid19.deliveryrequest.models.User;
import com.projects.covid19.deliveryrequest.services.DeliveryRequestService;
import com.projects.covid19.deliveryrequest.utils.RoleType;
import com.projects.covid19.deliveryrequest.utils.StatusTypeEnum;

@ExtendWith(SpringExtension.class)
@WebMvcTest(DeliveryRequestController.class)
@TestInstance(Lifecycle.PER_CLASS)
public class DeliveryRequestControllerAPITest {

	@Autowired
	private MockMvc mvc;

	@InjectMocks
	private DeliveryRequestController deliveryRequestController;
	
	@SpyBean
	private DeliveryRequestControllerAdvise deliveryRequestControllerAdvise;

	@MockBean
	private DeliveryRequestService deliveryRequestService;

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
		deliveryRequestController.setDeliveryRequestService(deliveryRequestService);
		interceptor.setJwtManager(jwtManager);
		mvc = MockMvcBuilders.standaloneSetup(deliveryRequestController)
				.setControllerAdvice(new DeliveryRequestControllerAdvise())
				.addInterceptors(interceptor).build();
	}

	@Test
	// @WithAnonymousUser
	public void shouldGetUnauthorizedAndNoHeaderTokenWithAnonymousUser() throws Exception {

		DeliveryRequest srequest = new DeliveryRequest();
		srequest.setItem("Item 1");
		srequest.setQuantity(5);
		srequest.setUnit("cans");
		srequest.setAcceptedDate(LocalDateTime.now().plusMinutes(10));
		
		mvc.perform(post("/api/delivery/add").with(csrf()).content(asJsonString(srequest))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized())
				.andExpect(header().doesNotExist("token"))
				.andExpect(jsonPath("$.status").value(HttpServletResponse.SC_UNAUTHORIZED))
				.andExpect(jsonPath("$.message").value("Unauthorized user or invalid token"));
	}
	
	@Test
	public void shouldReturnSuccessfulWithWithNewHeaderValidTokenWhenValidTokenIsPassed() throws Exception {

		String token = getAuthenticatedToken();
		
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
		
		
		//{"item":"Item 1","quantity":5,"unit":"cans","requestedDate":"10-04-2020 19:41","requesteeId":null,"requesterId":1,"accepterId":null,"acceptedDate":"10-04-2020 19:51","status":"REQUEST"}
		
		when(deliveryRequestService.createDeliveryRequest(srequest)).thenReturn(new DeliveryRequest());

		mvc.perform(post("/api/delivery/add").with(csrf()).header("Authorization", "Bearer " + token).content(asJsonString(srequest))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is2xxSuccessful())
				.andExpect(header().exists("token"));
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

	public static String asJsonString(final Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
