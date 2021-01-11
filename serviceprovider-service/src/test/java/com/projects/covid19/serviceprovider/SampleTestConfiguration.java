package com.projects.covid19.serviceprovider;

import java.util.Random;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleTestConfiguration {
	
	@Bean
	public Random random() {
		return new Random();
	}

}
