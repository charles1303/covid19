package com.projects.covid19.serviceitem.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Component;

import com.projects.covid19.serviceitem.models.ServiceCategory;

@Component
public interface ServiceCategoryRepository extends CrudRepository<ServiceCategory, Long>, QueryByExampleExecutor<ServiceCategory> {
	
	Optional<ServiceCategory> findByName(String name);

}
