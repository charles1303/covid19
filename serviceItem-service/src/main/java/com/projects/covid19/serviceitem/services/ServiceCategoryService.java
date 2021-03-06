package com.projects.covid19.serviceitem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.projects.covid19.serviceitem.models.ServiceCategory;
import com.projects.covid19.serviceitem.repositories.ServiceCategoryRepository;

@Service
public class ServiceCategoryService {
	
	@Autowired
	private ServiceCategoryRepository serviceCategoryRepository;
	
	public Iterable<ServiceCategory> getCategory(String name) {
		
		ServiceCategory category = new ServiceCategory();
		category.setName(name);
		
		ExampleMatcher matcher = ExampleMatcher.matching()
				.withMatcher("name", match -> match.exact())
				.withIgnoreCase(false)
				.withIgnorePaths("description","code");
		
		Example<ServiceCategory> found = Example.of(category, matcher);
		
		return serviceCategoryRepository.findAll(found);
		
		
		
	}

}
