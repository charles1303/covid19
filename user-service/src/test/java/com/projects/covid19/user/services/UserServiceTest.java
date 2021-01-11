package com.projects.covid19.user.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.projects.covid19.user.dto.requests.SignUpRequest;
import com.projects.covid19.user.exceptions.GeneralException;
import com.projects.covid19.user.models.Role;
import com.projects.covid19.user.models.User;
import com.projects.covid19.user.repositories.UserRepository;
import com.projects.covid19.user.utils.RoleType;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class UserServiceTest {
	
	@InjectMocks
	private UserService userService;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
    RoleService roleService;
    
	@Mock
    PasswordEncoder passwordEncoder;
	
	@Test
	public void registerUser_shouldReturnSavedUser_whenCalled() throws GeneralException {
		
		SignUpRequest signUpRequest = new SignUpRequest();
		signUpRequest.setEmail("user@emai.com");
		signUpRequest.setName("User1");
		signUpRequest.setUsername("user@emai.com");
		signUpRequest.setPassword("xxxxxx");
		
		User user = new User();

        user.setEmail(signUpRequest.getEmail());
        user.setName(signUpRequest.getName());
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(signUpRequest.getPassword());
		
		when(passwordEncoder.encode(any(String.class))).thenReturn(signUpRequest.getPassword());
		
		when(roleService.findByRoleType(any(RoleType.class))).thenReturn(Optional.of(new Role()));
		
		when(userRepository.save(any(User.class))).thenReturn(user);
		
		User saved = userService.registerUser(signUpRequest, RoleType.ROLE_USER);
		assertEquals(saved.getName(), user.getName());
	}

}
