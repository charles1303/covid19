package com.projects.covid19.serviceprovider.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.covid19.serviceprovider.dto.responses.ApiResponse;
import com.projects.covid19.serviceprovider.exceptions.GeneralException;
import com.projects.covid19.serviceprovider.models.ServiceCategory;
import com.projects.covid19.serviceprovider.services.ServiceCategoryService;

@RestController
@RequestMapping("/api/category")
public class ServiceCategoryController {
	
	@Autowired
	private ServiceCategoryService serviceCategoryService;
		
	@PostMapping("/add-category")
    public ResponseEntity<?> addCategory(@Valid @RequestBody ServiceCategory category) throws GeneralException {
		
		serviceCategoryService.saveServiceCategory(category);
        
        return ResponseEntity.ok(new ApiResponse(0, "Service Category saved successfully", category));
    }

}
