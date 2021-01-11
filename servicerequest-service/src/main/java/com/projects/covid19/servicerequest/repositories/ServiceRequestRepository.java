package com.projects.covid19.servicerequest.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.projects.covid19.servicerequest.models.ServiceRequest;
import com.projects.covid19.servicerequest.utils.StatusTypeEnum;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
	
	@Query(value = "select sr from ServiceRequest sr where sr.status = :status")
	Page<ServiceRequest> findByPageStatus(@Param("status") StatusTypeEnum status, Pageable pageable);
	
	@Query(value = "select count(sr) from ServiceRequest sr where sr.status = :status")
	long getCountByPageStatus(@Param("status") StatusTypeEnum status);
	
	@Modifying
	@Query(value = "update ServiceRequest sr set sr.status = :status, sr.accepterId = :accepterId, sr.acceptedDate = CURRENT_DATE where sr.id = :serviceRequestId")
	int updateRequestStatus(@Param("status") StatusTypeEnum status, @Param("accepterId") Long accepterId, @Param("serviceRequestId") Long serviceRequestId);

	

}
