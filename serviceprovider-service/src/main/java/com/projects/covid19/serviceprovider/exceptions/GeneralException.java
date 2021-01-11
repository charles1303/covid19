package com.projects.covid19.serviceprovider.exceptions;

public class GeneralException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GeneralException(){
		super("General exception. Please contact Admin!");
	}
	
	public GeneralException(String message){
		super(message);
	}

}
