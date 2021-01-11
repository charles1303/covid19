package com.projects.covid19.deliveryrequest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="spring")
@EnableConfigurationProperties
public class YamlConfig {

}
