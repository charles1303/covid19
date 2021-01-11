package com.projects.covid19.deliveryrequest.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.covid19.deliveryrequest.dto.requests.DeliveryRequestDto;
import com.projects.covid19.deliveryrequest.dto.responses.ApiResponse;
import com.projects.covid19.deliveryrequest.exceptions.GeneralException;
import com.projects.covid19.deliveryrequest.models.DeliveryRequest;
import com.projects.covid19.deliveryrequest.services.DeliveryRequestService;

import lombok.Setter;

@RestController
@RequestMapping("/api/delivery")
@Setter
public class DeliveryRequestController {
	
	@Autowired
	private DeliveryRequestService deliveryRequestService;
	
	@PostMapping("/add")
    public ResponseEntity<?> makeRequest(@Valid @RequestBody DeliveryRequestDto requestDto) throws GeneralException {
		
		deliveryRequestService.createDeliveryRequest(requestDto);
        
        return ResponseEntity.ok(new ApiResponse(0, "Delivery request sent successfully", requestDto));
    }
	
	@GetMapping
    public ResponseEntity<?> getRequests() throws GeneralException {
		
		List<DeliveryRequest> requests = deliveryRequestService.getDeliveryRequests();
        
        return ResponseEntity.ok(new ApiResponse(0, "Delivery requests retrieved successfully", requests));
    }
	
	@GetMapping("/{requestId}")
    public ResponseEntity<?> getRequestById(@PathVariable(name = "requestId") Long requestId) throws GeneralException {
		
		DeliveryRequest request = deliveryRequestService.getDeliveryRequestById(requestId);
        
        return ResponseEntity.ok(new ApiResponse(0, "Delivery request retrieved successfully", request));
    }
	
	
	
	@PutMapping("/update")
    public ResponseEntity<?> updateDeliveryRequestStatus(@Valid @RequestBody DeliveryRequestDto requestDto) throws GeneralException {
		
		deliveryRequestService.updateDeliveryStatus(requestDto);
        
        return ResponseEntity.ok(new ApiResponse(0, "Delivery request updated successfully", requestDto));
    }

}
