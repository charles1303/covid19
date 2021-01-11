package com.projects.covid19.notification.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class HandlerGeneralException extends ResponseStatusException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8818132199422960772L;

	public HandlerGeneralException(HttpStatus status, String message) {
        super(status, message);
    }

    public HandlerGeneralException(HttpStatus status, String message, Throwable e) {
        super(status, message, e);
    }

}
