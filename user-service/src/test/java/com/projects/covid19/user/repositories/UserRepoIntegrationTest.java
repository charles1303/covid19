package com.projects.covid19.user.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.projects.covid19.user.models.User;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserRepoIntegrationTest {
	
	
	@Autowired
	private UserRepository userRepository;
	
	
	@Test
	public void whenFindByEmail_thenReturnUser() {
		User user = new User();

        user.setEmail("user@emai.com");
        user.setName("User1");
        user.setUsername("user@emai.com");
        user.setPassword("xxxxxx");
        userRepository.save(user);

		// when
        User found = userRepository.findByEmail(user.getEmail()).get();

		// then
		assertEquals(found.getName(), user.getName());
	}


}
