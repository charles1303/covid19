package com.projects.covid19.serviceitem.config;

import org.modelmapper.ModelMapper;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.projects.covid19.serviceitem.interceptors.SecurityInterceptor;


@Configuration
@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = {"com.projects.covid19.serviceitem"})
public class AppConfig implements WebMvcConfigurer{
 
	@Bean
	public ModelMapper modelMapper() {
	    return new ModelMapper();
	}
	
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
     
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	    registry.addInterceptor(new SecurityInterceptor()).excludePathPatterns(
                "/api/service/ping").pathMatcher(new AntPathMatcher());
	}
}
