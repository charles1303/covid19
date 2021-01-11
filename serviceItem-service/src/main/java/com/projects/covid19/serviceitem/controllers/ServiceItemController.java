package com.projects.covid19.serviceitem.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projects.covid19.serviceitem.dto.responses.ApiResponse;
import com.projects.covid19.serviceitem.exceptions.GeneralException;
import com.projects.covid19.serviceitem.models.ServiceItem;
import com.projects.covid19.serviceitem.services.ServiceItemService;

import lombok.Setter;

@RestController
@RequestMapping("/api/service")
@Setter
public class ServiceItemController {
	
	@Value("${server.port}")
	private int port;
	
    @Autowired
    private Environment env;
	
	@Autowired
	private ServiceItemService serviceItemService;
	
	@GetMapping("/ping")
    public ResponseEntity<?> ping() throws GeneralException {
	        
        return ResponseEntity.ok(new ApiResponse(0, "Service Items service up and running!", env.getProperty("server.port")));
    }
	
	@PostMapping("/add-item")
    public ResponseEntity<?> addItem(@Valid @RequestBody ServiceItem item, @RequestParam("categoryId") Long categoryId) throws GeneralException {
		
		serviceItemService.saveServiceItem(item, categoryId);
        
        return ResponseEntity.ok(new ApiResponse(0, "Service Item saved successfully", item));
    }
	
	@GetMapping("/get-items")
    public ResponseEntity<?> getItemsByCategoryId(@RequestParam("categoryId") Long categoryId) throws GeneralException {
		
		Iterable<ServiceItem> items = serviceItemService.getByCategoryId(categoryId);
        
        return ResponseEntity.ok(new ApiResponse(0, "Service Items retrieved successfully",items));
    }
	
	@GetMapping("/get-item")
    public ResponseEntity<?> getItemsById(@RequestParam("itemId") Long itemId) throws GeneralException {
		
		Optional<ServiceItem> item = serviceItemService.getServiceItemById(itemId);
        
        return ResponseEntity.ok(new ApiResponse(0, "Service Item retrieved successfully",item));
    }
	
	@GetMapping("/search-item")
	public ResponseEntity<?> getValuesByKeysPatternScan(@RequestParam("searchItem") String searchItem) {
		List<String> itemNames = serviceItemService.getValuesByKeysPatternScan(searchItem);
		return ResponseEntity.ok(new ApiResponse(0, "Service Item(s) retrieved successfully",itemNames));
	}

}
