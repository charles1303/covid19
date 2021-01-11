package com.projects.covid19.servicerequest.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.projects.covid19.servicerequest.dto.requests.ServiceRequestDto;
import com.projects.covid19.servicerequest.models.ServiceRequest;
import com.projects.covid19.servicerequest.repositories.ServiceRequestRepository;
import com.projects.covid19.servicerequest.utils.StatusTypeEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ServiceRequestRepoTest {
	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

	
	@Autowired
	private ServiceRequestRepository serviceRequestRepository;
	
	@DisplayName("Service Request when saved should return saved Service Request")
	@Test
	public void whenServiceRequestIsSaved_thenReturnServiceRequest() {
		
		ServiceRequestDto requestDto = new ServiceRequestDto();
		requestDto.setItem("Item 1");
		requestDto.setQuantity(5);
		requestDto.setUnit("cans");
		requestDto.setRequestedDate("10-04-2020 19:41");
		requestDto.setRequesterId(1L);
		requestDto.setAcceptedDate("10-04-2020 19:51");
		requestDto.setStatus(StatusTypeEnum.REQUEST);
		
		ServiceRequest request = new ServiceRequest();
		request.setItem(requestDto.getItem());
		request.setQuantity(requestDto.getQuantity());
		request.setUnit(requestDto.getUnit());
		request.setRequestedDate(LocalDateTime.parse(requestDto.getRequestedDate(), formatter));
		request.setRequesterId(requestDto.getRequesterId());
		request.setRequesterUsername("user");
		request.setAcceptedDate(LocalDateTime.parse(requestDto.getAcceptedDate(), formatter));
		request.setStatus(requestDto.getStatus());
		
		assertNull(request.getId());
		
		ServiceRequest requestSaved = serviceRequestRepository.save(request);
		
		assertNotNull(requestSaved.getId());
		
		Duration duration = Duration.between(request.getRequestedDate(), request.getAcceptedDate());
		long diff = Math.abs(duration.toMinutes());
		
		assertEquals(diff, 10);
		log.error(request.getRequestedDate().toString());
		log.error(request.getAcceptedDate().toString());
		
		
	}
	
	@DisplayName("Service Requests when retrieved should return retrieved Service Requests")
	@Test
	public void whenfindAllIsCalled_thenReturnAllServiceRequests() {
		
		ServiceRequest request = new ServiceRequest();
		request.setItem("Item 1");
		request.setQuantity(5);
		request.setUnit("cans");
		request.setRequestedDate(LocalDateTime.now());
		request.setRequesterId(1L);
		request.setRequesterUsername("user");
		request.setAcceptedDate(LocalDateTime.now().plusMinutes(10));
		request.setStatus(StatusTypeEnum.REQUEST);
		
		ServiceRequest request2 = new ServiceRequest();
		request2.setItem("Item 2");
		request2.setQuantity(2);
		request2.setUnit("cartons");
		request2.setRequestedDate(LocalDateTime.now());
		request2.setRequesterId(2L);
		request2.setRequesterUsername("user");
		request2.setAcceptedDate(LocalDateTime.now().plusMinutes(20));
		request2.setStatus(StatusTypeEnum.ACCEPT);
		
		ServiceRequest requestSaved = serviceRequestRepository.save(request);
		
		ServiceRequest requestSaved2 = serviceRequestRepository.save(request2);
		
		List<ServiceRequest> requests = serviceRequestRepository.findAll();
		
		assertNotNull(requests);
		
		assertTrue(requests.size() == 2);
		assertTrue(requests.contains(requestSaved));
		assertTrue(requests.contains(requestSaved2));
			
		
	}
	
	@DisplayName("shoud Throw ConstraintException When Username Not Given")
	@Test
	public void shoudThrowConstraintExceptionWhenUsernameNotGiven() {
		Exception exc = assertThrows(
				ConstraintViolationException.class,
		() -> saveServiceRequest());
		//assertTrue(exc.getMessage().contains("Exception thrown"));
	}
	
	
	public void saveServiceRequest() throws ConstraintViolationException{
		
		
		
		ServiceRequestDto requestDto = new ServiceRequestDto();
		requestDto.setItem("Item 1");
		requestDto.setQuantity(5);
		requestDto.setUnit("cans");
		requestDto.setRequestedDate("10-04-2020 19:41");
		requestDto.setRequesterId(1L);
		requestDto.setAcceptedDate("10-04-2020 19:51");
		requestDto.setStatus(StatusTypeEnum.REQUEST);
		
		ServiceRequest request = new ServiceRequest();
		request.setItem(requestDto.getItem());
		request.setQuantity(requestDto.getQuantity());
		request.setUnit(requestDto.getUnit());
		request.setRequestedDate(LocalDateTime.parse(requestDto.getRequestedDate(), formatter));
		request.setRequesterId(requestDto.getRequesterId());
		request.setAcceptedDate(LocalDateTime.parse(requestDto.getAcceptedDate(), formatter));
		request.setStatus(requestDto.getStatus());
		
		serviceRequestRepository.saveAndFlush(request);
		
		
	}


}
