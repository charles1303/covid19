package com.projects.covid19.serviceitem.exceptions;

public class RecordNotFoundException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RecordNotFoundException(){
		super("Record(s) Not Found!");
	}

}
