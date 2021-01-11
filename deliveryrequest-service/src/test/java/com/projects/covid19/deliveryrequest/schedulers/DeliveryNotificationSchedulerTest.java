package com.projects.covid19.deliveryrequest.schedulers;

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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestTemplate;

import com.projects.covid19.deliveryrequest.SampleTestConfiguration;
import com.projects.covid19.deliveryrequest.services.DeliveryRequestService;


@SpringJUnitConfig(SampleTestConfiguration.class)
public class DeliveryNotificationSchedulerTest {
	
	@SpyBean
	DeliveryNotificationScheduler tasks;
	
	@MockBean
	private DeliveryRequestService deliveryRequestService;
	
	@MockBean
    private RestTemplate restTemplate;
	
	
	@Test
	public void shouldCallNotifyServiceProviderEndPointWhenDeliveryAgentsAreAvailable() throws URISyntaxException {
		doNothing().when(deliveryRequestService).callNotifyProvidersEndPoint(any(List.class));
				
		when(deliveryRequestService.getRandomServiceProviderDeliveryAgentIdsFromServiceRequestByStatus())
		.thenReturn(List.of(1L,2L,3L,4L,5L));
			await().atMost(Duration.TEN_SECONDS).untilAsserted(() -> {
			verify(tasks, atLeast(1)).scheduled();
			verify(deliveryRequestService, atLeast(1)).callNotifyProvidersEndPoint(any(List.class));
		});
	}
	
	@Test
	public void shouldNotCallNotifyServiceProviderEndPointWhenDeliveryAgentsAreUnAvailable() throws URISyntaxException {
		
		doNothing().when(deliveryRequestService).callNotifyProvidersEndPoint(any(List.class));
		when(deliveryRequestService.getRandomServiceProviderDeliveryAgentIdsFromServiceRequestByStatus())
		.thenReturn(new ArrayList<>());
			await().atMost(Duration.TEN_SECONDS).untilAsserted(() -> {
			verify(tasks, atLeast(1)).scheduled();
			verify(deliveryRequestService, never()).callNotifyProvidersEndPoint(any(List.class));
		});
	}

}
