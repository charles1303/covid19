package com.projects.covid19.deliveryrequest.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.projects.covid19.deliveryrequest.dto.requests.DeliveryRequestDto;
import com.projects.covid19.deliveryrequest.models.DeliveryRequest;
import com.projects.covid19.deliveryrequest.repositories.DeliveryRequestRepository;
import com.projects.covid19.deliveryrequest.utils.StatusTypeEnum;

import lombok.Setter;

@Service
@Setter
public class DeliveryRequestService {
	
	@Value("${service.provider.url}")
	private String serviceProvidertUrl;

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

	@Autowired
	private DeliveryRequestRepository deliveryRequestRepository;
	
	@Autowired
	private RestTemplate template;
	
	@Autowired
	private Random random;

	public DeliveryRequest createDeliveryRequest(DeliveryRequest request) {

		return deliveryRequestRepository.save(request);

	}

	public DeliveryRequest createDeliveryRequest(DeliveryRequestDto requestDto) {

		DeliveryRequest request = new DeliveryRequest();
		request.setServiceRequestId(requestDto.getServiceRequestId());
		request.setItem(requestDto.getItem());
		request.setQuantity(requestDto.getQuantity());
		request.setUnit(requestDto.getUnit());
		request.setAcceptedDate(LocalDateTime.parse(requestDto.getAcceptedDate(), formatter));
		requestDto.setStatus(requestDto.getStatus());
		request.setName(requestDto.getName());
		request.setPhoneNumber(requestDto.getPhoneNumber());
		request.setAddress(requestDto.getAddress());
		request.setStatus(requestDto.getStatus());

		return deliveryRequestRepository.save(request);

	}

	public List<DeliveryRequest> getDeliveryRequests() {

		return deliveryRequestRepository.findAll();

	}
	
	public DeliveryRequest getDeliveryRequestById(Long requestId) {

		return deliveryRequestRepository.getOne(requestId);

	}
	
	/**
	 * The returned data is sent to the service provider service
	 * for notifications to the service providers' delivery agents in queue
	 * @param deliveryStatus
	 * @param pageSize
	 * @return
	 */
	public List<Long> getRandomServiceProviderDeliveryAgentIdsFromServiceRequestByStatus(DeliveryRequestDto requestDto, int pageSize) {
		long qty = deliveryRequestRepository.getCountByPageDeliveryStatus(requestDto.getStatus());
		//int idx = random.nextInt((int)(qty/pageSize + 1));
		int idx = (int)(Math.random() * (qty/pageSize + 1));
		Page<DeliveryRequest> spPage = deliveryRequestRepository.findByPageDeliveryStatus(requestDto.getStatus(),PageRequest.of(idx, pageSize));
		
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
	 * for notifications to the service providers' delivery agents in queue
	 * @return
	 */
	public List<Long> getRandomServiceProviderDeliveryAgentIdsFromServiceRequestByStatus() {
		 int pageSize = 10;
		long qty = deliveryRequestRepository.getCountByPageDeliveryStatus(StatusTypeEnum.ACCEPT);
		int idx = (int)(Math.random() * (qty/pageSize + 1));
		Page<DeliveryRequest> spPage = deliveryRequestRepository.findByPageDeliveryStatus(StatusTypeEnum.ACCEPT,PageRequest.of(idx, pageSize));
		
		List<Long> spIds = new ArrayList<>();
		if(spPage.hasContent()) {
			spIds = spPage.getContent().stream()
			.map(sp -> sp.getAccepterId())
			.collect(Collectors.toList());
		}
		return spIds;
		
	}
	
	public int updateDeliveryStatus(DeliveryRequestDto requestDto) {
		return deliveryRequestRepository.updateDeliveryStatus(requestDto.getStatus(), requestDto.getAccepterId(), requestDto.getDeliveryRequestId());
	}
	
	@Async
	public void callNotifyProvidersEndPoint(List<Long> spIds) throws URISyntaxException {
		String uri = serviceProvidertUrl+"/notify-service-providers-pending-request";
		URI fooResourceUrl = new URI(uri);
		HttpEntity<List<Long>> request = new HttpEntity<>(spIds);
		template.postForObject(fooResourceUrl, request, Object.class);
		System.out.println("Called callEndPoint.....");
	}

}
