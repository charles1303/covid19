package com.projects.covid19.servicerequest.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.projects.covid19.servicerequest.dto.responses.ApiResponse;

@ControllerAdvice
public class ServiceRequestControllerAdvise extends ResponseEntityExceptionHandler{
	@ExceptionHandler(value = HttpStatusCodeException.class)
	public ResponseEntity<ApiResponse> handleHttpException(HttpStatusCodeException exception) {
		HttpStatus httpStatus = getHttpExceptionDetails(exception);
		ApiResponse response = new ApiResponse(httpStatus.value(), exception.getMessage());
		
	      return new ResponseEntity<>(response, httpStatus);
	   }
	
	@ExceptionHandler(value = ResponseStatusException.class)
	public ResponseEntity<ApiResponse> handleResponseStatusException(ResponseStatusException exception) {
		HttpStatus httpStatus = getHttpExceptionDetails(exception);
		ApiResponse response = new ApiResponse(httpStatus.value(), exception.getMessage());
		
	      return new ResponseEntity<>(response, httpStatus);
	   }
	
	@Override
	public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
		ApiResponse response = new ApiResponse(httpStatus.value(), exception.getLocalizedMessage());
		
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
		case 400 : 
			return HttpStatus.BAD_REQUEST;
		default:
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
	}
	
	private HttpStatus getHttpExceptionDetails(ResponseStatusException exception) {
		int code = exception.getStatus().value();
		
		switch (code) {
		case 404 : 
			return HttpStatus.NOT_FOUND;
		case 400 : 
			return HttpStatus.BAD_REQUEST;
		default:
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
	}	
	
}


