package com.projects.covid19.servicerequest.utils;

import lombok.Getter;

@Getter
public enum StatusTypeEnum {
	
		REQUEST("REQUESTED"), ACCEPT("ACCEPTED"), DELIVERY("DELIVERED");
	
	private String code;
	
	private StatusTypeEnum(String code) {
		this.code = code;
	}
	
}
