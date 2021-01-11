package com.projects.covid19.servicerequest.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.projects.covid19.servicerequest.dto.requests.ServiceRequestDto;
import com.projects.covid19.servicerequest.kafka.producers.ServiceRequestKafkaPublisher;
import com.projects.covid19.servicerequest.models.ServiceRequest;
import com.projects.covid19.servicerequest.repositories.ServiceRequestRepository;
import com.projects.covid19.servicerequest.utils.StatusTypeEnum;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Setter @Slf4j
public class ServiceRequestService {
	
	@Value("${service.provider.url}")
	private String serviceProvidertUrl;

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;
	
	@Autowired
	private RestTemplate template;
	
	@Autowired
	private ServiceRequestKafkaPublisher kafkaPublisher;

	public ServiceRequest createServiceRequest(ServiceRequest request) {

		return serviceRequestRepository.save(request);

	}

	public ServiceRequest createServiceRequest(ServiceRequestDto requestDto, String username) {

		ServiceRequest request = new ServiceRequest();
		request.setItem(requestDto.getItem());
		request.setQuantity(requestDto.getQuantity());
		request.setUnit(requestDto.getUnit());
		request.setRequestedDate(LocalDateTime.parse(requestDto.getRequestedDate(), formatter));
		request.setRequesterId(requestDto.getRequesterId());
		request.setRequesterUsername(username);
		request.setAcceptedDate(LocalDateTime.parse(requestDto.getAcceptedDate(), formatter));
		request.setStatus(requestDto.getStatus());

		return serviceRequestRepository.saveAndFlush(request);

	}

	public List<ServiceRequest> getServiceRequests() {

		return serviceRequestRepository.findAll();

	}
	
	public ServiceRequest getServiceRequestById(Long requestId) {

		return serviceRequestRepository.getOne(requestId);

	}
	
	/**
	 * The returned data is sent to the service provider service
	 * for notifications to the service providers in queue
	 * @param status
	 * @param pageSize
	 * @return
	 */
	public List<Long> getRandomServiceProviderIdsFromServiceRequestByStatus(ServiceRequestDto requestDto, int pageSize) {
		long qty = serviceRequestRepository.getCountByPageStatus(requestDto.getStatus());
		int idx = (int)(Math.random() * (qty/pageSize + 1));
		Page<ServiceRequest> spPage = serviceRequestRepository.findByPageStatus(requestDto.getStatus(),PageRequest.of(idx, pageSize));
		
		List<Long> spIds = new ArrayList<>();
		if(spPage.hasContent()) {
			spIds = spPage.getContent().stream()
			.map(sp -> sp.getAccepterId())
			.collect(Collectors.toList());
		}
		return spIds;
		
	}
	
	/**
	 * The returned data is sent to the service provider service
	 * for notifications to the service providers in queue
	 * @return
	 */
	public List<Long> getRandomServiceProviderIdsFromServiceRequestByStatus() {
		int pageSize = 10;
		long qty = serviceRequestRepository.getCountByPageStatus(StatusTypeEnum.ACCEPT);
		int idx = (int)(Math.random() * (qty/pageSize + 1));
		Page<ServiceRequest> spPage = serviceRequestRepository.findByPageStatus(StatusTypeEnum.ACCEPT,PageRequest.of(idx, pageSize));
		
		List<Long> spIds = new ArrayList<>();
		if(spPage.hasContent()) {
			spIds = spPage.getContent().stream()
			.map(sp -> sp.getAccepterId())
			.collect(Collectors.toList());
		}
		return spIds;
		
	}

	public int updateServiceStatus(ServiceRequestDto requestDto) {
		return serviceRequestRepository.updateRequestStatus(requestDto.getStatus(), requestDto.getAccepterId(), requestDto.getServiceRequestId());
	}
	
	@Async
	public void callNotifyProvidersEndPoint(List<Long> spIds) throws URISyntaxException {
		String uri = serviceProvidertUrl+"/notify-service-providers-pending-request";
		URI fooResourceUrl = new URI(uri);
		HttpEntity<List<Long>> request = new HttpEntity<>(spIds);
		template.postForObject(fooResourceUrl, request, Object.class);
		log.debug("Called callEndPoint.....");
	}
	
	public void sendToKafka(ArrayList<Long> spIds) {
		kafkaPublisher.sendMessage(spIds);
	}
		
}
