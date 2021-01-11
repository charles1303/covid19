package com.projects.covid19.servicerequest.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.projects.covid19.servicerequest.components.CustomUserDetails;
import com.projects.covid19.servicerequest.components.JwtManager;
import com.projects.covid19.servicerequest.controllers.ServiceRequestController;
import com.projects.covid19.servicerequest.dto.requests.ServiceRequestDto;
import com.projects.covid19.servicerequest.dto.responses.ApiResponse;
import com.projects.covid19.servicerequest.exceptions.ServiceRequestControllerAdvise;
import com.projects.covid19.servicerequest.interceptors.SecurityInterceptor;
import com.projects.covid19.servicerequest.models.Role;
import com.projects.covid19.servicerequest.models.ServiceRequest;
import com.projects.covid19.servicerequest.models.User;
import com.projects.covid19.servicerequest.services.ServiceRequestService;
import com.projects.covid19.servicerequest.utils.RoleType;
import com.projects.covid19.servicerequest.utils.StatusTypeEnum;

//@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test") 
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@EmbeddedKafka(partitions = 1,controlledShutdown = true, brokerProperties={
        "log.dir=target/embedded-kafka"
})
public class ServiceRequestIntegrationTest {
	
	@Autowired
	private MockMvc mvc;

	@Autowired
	private ServiceRequestController serviceRequestController;
	
	@SpyBean
	private ServiceRequestControllerAdvise serviceRequestControllerAdvise;

	@Autowired
	private ServiceRequestService serviceRequestService;

	@SpyBean
	private JwtManager jwtManager;

	@Autowired
	static ObjectMapper mapper = new ObjectMapper();
	
	@MockBean
	private PasswordEncoder passwordEncoder;
	
	@BeforeAll
	public void setup() {

		SecurityInterceptor interceptor = new SecurityInterceptor();
		serviceRequestController.setServiceRequestService(serviceRequestService);
		interceptor.setJwtManager(jwtManager);
		mvc = MockMvcBuilders.standaloneSetup(serviceRequestController)
				.setControllerAdvice(new ServiceRequestControllerAdvise())
				.addInterceptors(interceptor).build();
	}

	
	
	@Test
	public void shouldSaveAndRetrieveSavedServiceRequests() throws Exception {
		
		mapper.registerModule(new JavaTimeModule());

		String token = getAuthenticatedToken();
		
		ServiceRequestDto srequest = new ServiceRequestDto();
		srequest.setItem("Item 1");
		srequest.setQuantity(5);
		srequest.setUnit("cans");
		srequest.setRequestedDate("10-04-2020 19:41");
		srequest.setRequesterId(1L);
		srequest.setAcceptedDate("10-04-2020 19:51");
		srequest.setStatus(StatusTypeEnum.REQUEST);
		
		
		mvc.perform(post("/api/request/add").with(csrf()).header("Authorization", "Bearer " + token).content(asJsonString(srequest))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is2xxSuccessful())
				.andExpect(header().exists("token"));
		
		
		ResultActions actions = mvc.perform(get("/api/request").with(csrf()).header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is2xxSuccessful())
				.andExpect(header().exists("token"));
		
		String response = actions.andReturn().getResponse().getContentAsString();

		ApiResponse apiResponse = mapper.readValue(response, ApiResponse.class);

		assertThat(apiResponse.getMessage()).isEqualTo("Service requests retrieved successfully");
		assertThat(apiResponse.getStatus()).isEqualTo(0);

		List<ServiceRequest> requests = mapper.convertValue(apiResponse.getResult(), new TypeReference<List<ServiceRequest>>(){});
		assertTrue(requests.get(0).getItem().equalsIgnoreCase(srequest.getItem()));
		assertTrue(requests.get(0).getQuantity() == srequest.getQuantity());
		assertEquals(requests.get(0).getStatus(), srequest.getStatus());

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
