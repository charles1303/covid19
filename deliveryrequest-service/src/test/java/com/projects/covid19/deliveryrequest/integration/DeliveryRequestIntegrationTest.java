package com.projects.covid19.deliveryrequest.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.projects.covid19.deliveryrequest.components.CustomUserDetails;
import com.projects.covid19.deliveryrequest.components.JwtManager;
import com.projects.covid19.deliveryrequest.controllers.DeliveryRequestController;
import com.projects.covid19.deliveryrequest.dto.requests.DeliveryRequestDto;
import com.projects.covid19.deliveryrequest.dto.responses.ApiResponse;
import com.projects.covid19.deliveryrequest.exceptions.DeliveryRequestControllerAdvise;
import com.projects.covid19.deliveryrequest.interceptors.SecurityInterceptor;
import com.projects.covid19.deliveryrequest.models.DeliveryRequest;
import com.projects.covid19.deliveryrequest.models.Role;
import com.projects.covid19.deliveryrequest.models.User;
import com.projects.covid19.deliveryrequest.services.DeliveryRequestService;
import com.projects.covid19.deliveryrequest.utils.RoleType;
import com.projects.covid19.deliveryrequest.utils.StatusTypeEnum;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test") 
public class DeliveryRequestIntegrationTest {
	
	@Autowired
	private MockMvc mvc;

	@Autowired
	private DeliveryRequestController deliveryRequestController;
	
	@SpyBean
	private DeliveryRequestControllerAdvise deliveryRequestControllerAdvise;

	@Autowired
	private DeliveryRequestService deliveryRequestService;

	@SpyBean
	private JwtManager jwtManager;

	@Autowired
	static ObjectMapper mapper = new ObjectMapper();
	
	@MockBean
	private PasswordEncoder passwordEncoder;
	
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
	public void shouldSaveAndRetrieveSavedDeliveryRequests() throws Exception {
		
		mapper.registerModule(new JavaTimeModule());

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
		
		
		mvc.perform(post("/api/delivery/add").with(csrf()).header("Authorization", "Bearer " + token).content(asJsonString(srequest))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is2xxSuccessful())
				.andExpect(header().exists("token"));
		
		
		ResultActions actions = mvc.perform(get("/api/delivery").with(csrf()).header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is2xxSuccessful())
				.andExpect(header().exists("token"));
		
		String response = actions.andReturn().getResponse().getContentAsString();

		ApiResponse apiResponse = mapper.readValue(response, ApiResponse.class);

		assertThat(apiResponse.getMessage()).isEqualTo("Delivery requests retrieved successfully");
		assertThat(apiResponse.getStatus()).isEqualTo(0);

		List<DeliveryRequest> requests = mapper.convertValue(apiResponse.getResult(), new TypeReference<List<DeliveryRequest>>(){});
		assertTrue(requests.get(0).getItem().equalsIgnoreCase(srequest.getItem()));
		assertTrue(requests.get(0).getQuantity() == srequest.getQuantity());
		
	}
	
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
