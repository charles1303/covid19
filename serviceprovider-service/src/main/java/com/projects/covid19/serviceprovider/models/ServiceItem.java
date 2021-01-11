package com.projects.covid19.serviceprovider.models;

import java.io.Serializable;

import javax.persistence.Id;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import lombok.Getter;
import lombok.Setter;

@RedisHash("service_item")
@Getter @Setter
public class ServiceItem implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1185994925841307967L;
	@Id
	private Long id;
	@Indexed
	private String name;
	private String description;
	private String code;
	@Indexed
	private Long serviceCategoryId;

}
