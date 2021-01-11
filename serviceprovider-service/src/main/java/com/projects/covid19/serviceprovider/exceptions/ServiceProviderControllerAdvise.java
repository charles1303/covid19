package com.projects.covid19.serviceprovider.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;

import com.projects.covid19.serviceprovider.dto.responses.ApiResponse;

@ControllerAdvice
public class ServiceProviderControllerAdvise {
	@ExceptionHandler(value = HttpStatusCodeException.class)
	public ResponseEntity<ApiResponse> handleHttpException(HttpStatusCodeException exception) {
		HttpStatus httpStatus = getHttpExceptionDetails(exception);
		ApiResponse response = new ApiResponse(httpStatus.value(), exception.getMessage());
		
	      return new ResponseEntity<>(response, httpStatus);
	   }
	
	@ExceptionHandler(value = GeneralException.class)
	public ResponseEntity<ApiResponse> handleGeneralException(GeneralException exception) {
		HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		ApiResponse response = new ApiResponse(httpStatus.value(), exception.getMessage());
		
	      return new ResponseEntity<>(response, httpStatus);
	   }
	
	@ExceptionHandler(value = RecordNotFoundException.class)
	public ResponseEntity<ApiResponse> handleRecordsNotFoundException(RecordNotFoundException exception) {
		HttpStatus httpStatus = HttpStatus.NOT_FOUND;
		ApiResponse response = new ApiResponse(httpStatus.value(), exception.getMessage());
		
	      return new ResponseEntity<>(response, httpStatus);
	   }
	
	private HttpStatus getHttpExceptionDetails(HttpStatusCodeException exception) {
		int code = exception.getRawStatusCode();
		
		switch (code) {
		case 404 : 
			return HttpStatus.NOT_FOUND;
		default:
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
	}

}


