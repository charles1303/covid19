package com.projects.covid19.servicerequest.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.projects.covid19.servicerequest.interceptors.SecurityInterceptor;


@Configuration
@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = {"com.projects.covid19.servicerequest"})
public class AppConfig implements WebMvcConfigurer{
 
	@Bean
	public ModelMapper modelMapper() {
	    return new ModelMapper();
	}
	
	@Bean
	public RestTemplate rest() {
		return new RestTemplate();
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	    registry.addInterceptor(new SecurityInterceptor());
	}
     
}
