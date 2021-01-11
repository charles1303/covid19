package com.projects.covid19.serviceprovider.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import com.projects.covid19.serviceprovider.models.ServiceItem;

public interface ServiceItemRepository extends CrudRepository<ServiceItem, Long> , QueryByExampleExecutor<ServiceItem>{
	
	Optional<ServiceItem> findByName(String name);

}
