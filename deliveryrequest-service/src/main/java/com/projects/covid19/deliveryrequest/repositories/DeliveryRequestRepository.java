package com.projects.covid19.deliveryrequest.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.projects.covid19.deliveryrequest.models.DeliveryRequest;
import com.projects.covid19.deliveryrequest.utils.StatusTypeEnum;

public interface DeliveryRequestRepository extends JpaRepository<DeliveryRequest, Long> {
	
	@Query(value = "select dr from DeliveryRequest dr where dr.status = :status")
	Page<DeliveryRequest> findByPageDeliveryStatus(@Param("status") StatusTypeEnum status, Pageable pageable);
	
	@Query(value = "select count(dr) from DeliveryRequest dr where dr.status = :status")
	long getCountByPageDeliveryStatus(@Param("status") StatusTypeEnum status);
	
	@Modifying
	@Query(value = "update DeliveryRequest dr set dr.status = :status, dr.accepterId = :accepterId, dr.acceptedDate = CURRENT_DATE where dr.id = :deliveryRequestId")
	int updateDeliveryStatus(@Param("status") StatusTypeEnum status, @Param("accepterId") Long accepterId, @Param("deliveryRequestId") Long deliveryRequestId);

}
