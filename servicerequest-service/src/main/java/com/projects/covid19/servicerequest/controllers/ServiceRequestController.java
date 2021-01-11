package com.projects.covid19.servicerequest.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.covid19.servicerequest.components.JwtManager;
import com.projects.covid19.servicerequest.dto.requests.ServiceRequestDto;
import com.projects.covid19.servicerequest.dto.responses.ApiResponse;
import com.projects.covid19.servicerequest.exceptions.GeneralException;
import com.projects.covid19.servicerequest.models.ServiceRequest;
import com.projects.covid19.servicerequest.services.ServiceRequestService;

import lombok.Setter;

@RestController
@RequestMapping("/api/request")
@Setter
public class ServiceRequestController {
	
	@Autowired
	private ServiceRequestService serviceRequestService;
	
	@Autowired
	private JwtManager jwtManager;

	
	@PostMapping("/add")
    public ResponseEntity<?> makeRequest(@Valid @RequestBody ServiceRequestDto requestDto, @RequestHeader HttpHeaders headers) throws GeneralException {
		String username = getUsernameFromJwtHeader(headers);
		serviceRequestService.createServiceRequest(requestDto, username);
        
        return ResponseEntity.ok(new ApiResponse(0, "Service request sent successfully", requestDto));
    }
	
	@GetMapping
    public ResponseEntity<?> getRequests() throws GeneralException {
		
		List<ServiceRequest> requests = serviceRequestService.getServiceRequests();
        
        return ResponseEntity.ok(new ApiResponse(0, "Service requests retrieved successfully", requests));
    }
	
	@GetMapping("/{requestId}")
    public ResponseEntity<?> getRequestById(@PathVariable(name = "requestId") Long requestId) throws GeneralException {
		
		ServiceRequest request = serviceRequestService.getServiceRequestById(requestId);
        
        return ResponseEntity.ok(new ApiResponse(0, "Service request retrieved successfully", request));
    }
	
	@PutMapping("/update")
    public ResponseEntity<?> updateServiceRequestStatus(@Valid @RequestBody ServiceRequestDto requestDto) throws GeneralException {
		
		serviceRequestService.updateServiceStatus(requestDto);
        
        return ResponseEntity.ok(new ApiResponse(0, "Service request updated successfully", requestDto));
    }
	
	
	private String getUsernameFromJwtHeader(HttpHeaders headers) {
		String bearerToken = headers.getFirst("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return jwtManager.getUsernameFromJWT(bearerToken.substring(7, bearerToken.length()));
		}
		return null;
	}
	

}
