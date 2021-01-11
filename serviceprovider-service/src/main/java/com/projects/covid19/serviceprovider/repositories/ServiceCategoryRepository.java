package com.projects.covid19.serviceprovider.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Component;

import com.projects.covid19.serviceprovider.models.ServiceCategory;

@Component
public interface ServiceCategoryRepository extends CrudRepository<ServiceCategory, Long>, QueryByExampleExecutor<ServiceCategory> {
	
	Optional<ServiceCategory> findByName(String name);
	
	@Query(value = "select sc from ServiceCategory sc where sc.id in (:categoryIds)")
	List<ServiceCategory> findByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

}
