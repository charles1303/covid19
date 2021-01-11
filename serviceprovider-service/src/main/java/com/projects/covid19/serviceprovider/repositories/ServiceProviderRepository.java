package com.projects.covid19.serviceprovider.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.projects.covid19.serviceprovider.models.ServiceProvider;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long>{
	
	Optional<ServiceProvider> findByEmail(String email);
	
	Optional<ServiceProvider> findByName(String name);
	
	@Query(value = "select sp from ServiceProvider sp inner join sp.categories category where sp.id = :providerId and category.code = :categoryCode")
	Optional<ServiceProvider> findByCategoryCodeAndProviderId(@Param("categoryCode") String categoryCode, @Param("providerId") Long providerId);
	
	
	@Query(value = "select sp from ServiceProvider sp inner join sp.categories category where category.code = :categoryCode")
	List<ServiceProvider> findByCategoryCode(@Param("categoryCode") String categoryCode);
	
	@Query(value = "select sp from ServiceProvider sp join fetch sp.categories category where sp.id = :providerId")
	Optional<ServiceProvider> loadProviderWithCategories(@Param("providerId") Long providerId);
	
	long count();
	
	Page<ServiceProvider> findAll(Pageable pageable);
	
	@Query(value = "select sp from ServiceProvider sp inner join sp.categories category where category.code = :categoryCode")
	Page<ServiceProvider> findByPageCategoryCode(@Param("categoryCode") String categoryCode, Pageable pageable);
	
	@Query(value = "select count(sp) from ServiceProvider sp inner join sp.categories category where category.code = :categoryCode")
	long getCountByPageCategoryCode(@Param("categoryCode") String categoryCode);
	
	
	@Query(value = "select sp from ServiceProvider sp where sp.id in (:idList)")
	Page<ServiceProvider> findByPageIds(@Param("idList") List<Long> idList, Pageable pageable);
	
	@Query(value = "select count(sp) from ServiceProvider sp where sp.id in (:idList)")
	long getCountByPageIds(@Param("idList") List<Long> idList);
	
		
}
