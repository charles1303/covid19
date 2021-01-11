package com.projects.covid19.serviceprovider.controllers;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projects.covid19.serviceprovider.dto.requests.ServiceProviderCategoryAddRequest;
import com.projects.covid19.serviceprovider.dto.responses.ApiResponse;
import com.projects.covid19.serviceprovider.exceptions.GeneralException;
import com.projects.covid19.serviceprovider.models.ServiceProvider;
import com.projects.covid19.serviceprovider.services.ServiceProviderService;

import lombok.Setter;

@RestController
@RequestMapping("/api/provider")
@Setter
public class ServiceProviderController {
	
	@Autowired
	private ServiceProviderService serviceProviderService;
	
	@PostMapping("/add-provider")
    public ResponseEntity<?> addProvider(@Valid @RequestBody ServiceProvider provider) throws GeneralException {
		
		serviceProviderService.saveServiceProvider(provider);
        
        return ResponseEntity.ok(new ApiResponse(0, "Service Provider saved successfully", provider));
    }
	
	@PutMapping("/add-cat-to-provider")
    public ResponseEntity<ApiResponse> addCategoryToProvider(@Valid @RequestBody ServiceProviderCategoryAddRequest request) throws GeneralException {
		
		ServiceProvider provider = serviceProviderService.addCategoryToProvider(request.getProviderId(), request.getCategoryIds().get(0));
        
        return ResponseEntity.ok(new ApiResponse(0, "Service Provider updated successfully", provider));
    }
	
	@PutMapping("/add-cats-to-provider")
    public ResponseEntity<?> addCategoriesToProvider(@Valid @RequestBody ServiceProviderCategoryAddRequest request) throws GeneralException {
		
		ServiceProvider provider = serviceProviderService.addCategoriesToProvider(request.getProviderId(), request.getCategoryIds());
        
        return ResponseEntity.ok(new ApiResponse(0, "Service Provider updated successfully", provider));
    }
	
	@GetMapping("/get-providers")
    public ResponseEntity<?> getServiceProviders() throws GeneralException {
		
		Iterable<ServiceProvider> providers = serviceProviderService.getServiceProviders();
        
        return ResponseEntity.ok(new ApiResponse(0, "Service providers retrieved successfully",providers));
    }
	
	@GetMapping("/get-provider")
    public ResponseEntity<?> getItemsById(@RequestParam("providerId") Long providerId) throws GeneralException {
		
		Optional<ServiceProvider> provider = serviceProviderService.getServiceProvider(providerId);
        
        return ResponseEntity.ok(new ApiResponse(0, "Service provider retrieved successfully",provider));
    }
	
	@GetMapping("/get-provider-by-cat")
    public ResponseEntity<?> findByCategoryCode(@RequestParam("categoryCode") String categoryCode) throws GeneralException {
		
		List<ServiceProvider> providers = serviceProviderService.findByCategoryCode(categoryCode);
        
        return ResponseEntity.ok(new ApiResponse(0, "Service providers retrieved successfully",providers));
    }
	
	@GetMapping("/get-provider-by-cat-and-provider")
    public ResponseEntity<?> findByCategoryCodeAndProviderId(@RequestParam("providerId") Long providerId, @RequestParam("categoryCode") String categoryCode) throws GeneralException {
		
		Optional<ServiceProvider> provider = serviceProviderService.findByCategoryCodeAndProviderId(categoryCode, providerId);
        
        return ResponseEntity.ok(new ApiResponse(0, "Service provider retrieved successfully",provider));
    }
	
	@PutMapping("/notify-service-providers-pending-request")
    public ResponseEntity<?> notifyServiceProvidersPendingRequest(@RequestBody List<Long> spIds) throws GeneralException, URISyntaxException {
		
		serviceProviderService.notifyServiceProvidersForPendingRequests(spIds);
        
        return ResponseEntity.ok(new ApiResponse(0, "Request sent for notifying service providers successfully"));
    }

}
