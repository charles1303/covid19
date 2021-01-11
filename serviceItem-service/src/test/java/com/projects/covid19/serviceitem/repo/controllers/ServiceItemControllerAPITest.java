package com.projects.covid19.serviceitem.repo.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.covid19.serviceitem.components.CustomUserDetails;
import com.projects.covid19.serviceitem.components.JwtManager;
import com.projects.covid19.serviceitem.controllers.ServiceItemController;
import com.projects.covid19.serviceitem.dto.responses.ApiResponse;
import com.projects.covid19.serviceitem.interceptors.SecurityInterceptor;
import com.projects.covid19.serviceitem.models.Role;
import com.projects.covid19.serviceitem.models.User;
import com.projects.covid19.serviceitem.services.ServiceItemService;
import com.projects.covid19.serviceitem.utils.RoleType;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ServiceItemController.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ServiceItemControllerAPITest {
	
	@Autowired
	private MockMvc mvc;

	@InjectMocks
	private ServiceItemController serviceItemController;

	@MockBean
	private ServiceItemService serviceItemService;

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
		serviceItemController.setServiceItemService(serviceItemService);
		interceptor.setJwtManager(jwtManager);
		mvc = MockMvcBuilders.standaloneSetup(serviceItemController)
				.addInterceptors(interceptor).build();
	}
	
	@Test
	public void shouldReturnItemsWithSuppliedSearchNamePattern() throws Exception {

		
		String token = getAuthenticatedToken();

		
		when(serviceItemService.getValuesByKeysPatternScan(any(String.class))).thenReturn(List.of("Item1","Item2","Item3"));

		ResultActions actions = mvc.perform(get("/api/service/search-item?searchItem=xxxxx").with(csrf())
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.status").value(0))
				.andExpect(jsonPath("$.message").value("Service Item(s) retrieved successfully"));
		
		String response = actions.andReturn().getResponse().getContentAsString();

		ApiResponse apiResponse = mapper.readValue(response, ApiResponse.class);

		List<String> requests = mapper.convertValue(apiResponse.getResult(), new TypeReference<List<String>>(){});
		assertTrue(requests.contains("Item1"));
		assertTrue(requests.contains("Item2"));
		assertTrue(requests.contains("Item3"));

		

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
