package com.projects.covid19.serviceitem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter @Setter
public class RedisPropertiesConfig {
	
	private int redisPort;
	private String redisHost;
	
	public RedisPropertiesConfig( @Value("${spring.redis.port}") int redisPort, 
			@Value("${spring.redis.host}") String redisHost) {
		this.redisPort = redisPort;
		this.redisHost = redisHost;
	}

}
