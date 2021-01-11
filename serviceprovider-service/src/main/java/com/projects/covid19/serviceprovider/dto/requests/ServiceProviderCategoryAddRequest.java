package com.projects.covid19.serviceprovider.dto.requests;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class ServiceProviderCategoryAddRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1636828330970263041L;
	
	private Long providerId;
	private List<Long> categoryIds = new ArrayList<>();

}
