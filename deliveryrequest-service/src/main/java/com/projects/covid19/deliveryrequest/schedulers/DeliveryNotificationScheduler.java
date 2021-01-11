package com.projects.covid19.deliveryrequest.schedulers;

import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.projects.covid19.deliveryrequest.services.DeliveryRequestService;

import lombok.Setter;

@Component
@Setter
public class DeliveryNotificationScheduler {
	
	@Autowired
	private DeliveryRequestService deliveryRequestService;
	
	@Scheduled(fixedRateString = "${scheduler.delivery.notification.fixedRate:5000}")
    public void scheduled() throws URISyntaxException {
        System.out.println("Called Delivery Scheduler.....");
        List<Long> spIds = deliveryRequestService.getRandomServiceProviderDeliveryAgentIdsFromServiceRequestByStatus();
        
        if(!spIds.isEmpty()) {
        	deliveryRequestService.callNotifyProvidersEndPoint(spIds);
        }
	}

}
