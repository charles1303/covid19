package com.projects.covid19.notification.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class GeneralException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private HttpStatus httpStatus;

	public GeneralException(){
		super("General exception. Please contact Admin!");
	}
	
	public GeneralException(String message){
		super(message);
	}

	public GeneralException(HttpStatus httpStatus, String message, Throwable cause) {
		super(message, cause);
		this.httpStatus = httpStatus;
	}
	
	public GeneralException(HttpStatus httpStatus, String message) {
		super(message);
		this.httpStatus = httpStatus;
	}

}
