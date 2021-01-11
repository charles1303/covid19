package com.projects.covid19.servicerequest.schedulers;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.awaitility.Duration;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestTemplate;

import com.projects.covid19.servicerequest.SampleTestConfiguration;
import com.projects.covid19.servicerequest.services.ServiceRequestService;


@SpringJUnitConfig(SampleTestConfiguration.class)
public class RequestNotificationSchedulerTest {
	
	@SpyBean
	RequestNotificationScheduler tasks;
	
	@MockBean
	private ServiceRequestService serviceRequestService;
	
	@MockBean
    private RestTemplate restTemplate;
	
	
	@Test
	public void shouldCallNotifyServiceProviderEndPointWhenDeliveryAgentsAreAvailable() throws URISyntaxException {
		doNothing().when(serviceRequestService).callNotifyProvidersEndPoint(any(List.class));
				
		when(serviceRequestService.getRandomServiceProviderIdsFromServiceRequestByStatus())
		.thenReturn(List.of(1L,2L,3L,4L,5L));
			await().atMost(Duration.TEN_SECONDS).untilAsserted(() -> {
			verify(tasks, atLeast(1)).scheduled();
			verify(serviceRequestService, atLeast(1)).callNotifyProvidersEndPoint(any(List.class));
		});
	}
	
	@Test
	public void shouldCallNotNotifyServiceProviderEndPointWhenDeliveryAgentsAreUnAvailable() throws URISyntaxException {
		
		doNothing().when(serviceRequestService).callNotifyProvidersEndPoint(any(List.class));
		when(serviceRequestService.getRandomServiceProviderIdsFromServiceRequestByStatus())
		.thenReturn(new ArrayList<>());
			await().atMost(Duration.TEN_SECONDS).untilAsserted(() -> {
			verify(tasks, atLeast(1)).scheduled();
			verify(serviceRequestService, never()).callNotifyProvidersEndPoint(any(List.class));
		});
	}
	
	@Test
	public void shouldCallsendToKafkaWhenDeliveryAgentsAreAvailable() {
		doNothing().when(serviceRequestService).sendToKafka(any(ArrayList.class));
				
		when(serviceRequestService.getRandomServiceProviderIdsFromServiceRequestByStatus())
		.thenReturn(List.of(1L,2L,3L,4L,5L));
			await().atMost(Duration.TEN_SECONDS).untilAsserted(() -> {
			verify(tasks, atLeast(1)).scheduled();
			verify(serviceRequestService, atLeast(1)).sendToKafka(any(ArrayList.class));
		});
	}
	
	@Test
	public void shouldCallNotsendToKafkaWhenDeliveryAgentsAreUnAvailable() {
		
		doNothing().when(serviceRequestService).sendToKafka(any(ArrayList.class));
		when(serviceRequestService.getRandomServiceProviderIdsFromServiceRequestByStatus())
		.thenReturn(new ArrayList<>());
			await().atMost(Duration.TEN_SECONDS).untilAsserted(() -> {
			verify(tasks, atLeast(1)).scheduled();
			verify(serviceRequestService, never()).sendToKafka(any(ArrayList.class));
		});
	}

}
