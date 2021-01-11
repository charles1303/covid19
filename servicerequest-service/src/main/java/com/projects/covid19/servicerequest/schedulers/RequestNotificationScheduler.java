package com.projects.covid19.servicerequest.schedulers;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.projects.covid19.servicerequest.services.ServiceRequestService;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@Setter @Slf4j
public class RequestNotificationScheduler {
	
		
	@Autowired
	private ServiceRequestService serviceRequestService;
		
	@Scheduled(fixedRateString = "${scheduler.request.notification.fixedRate:5000}")
    public void scheduled() throws URISyntaxException {
		log.debug("Called Scheduler.....");
        List<Long> spIds = serviceRequestService.getRandomServiceProviderIdsFromServiceRequestByStatus();
        if(!spIds.isEmpty()) {
        	serviceRequestService.callNotifyProvidersEndPoint(spIds);
        }
        
    }
	
	@Scheduled(fixedRateString = "${scheduler.request.notification.fixedRate:5000}")
    public void scheduledForKafka() {
		log.debug("Called scheduledForKafka.....");
        List<Long> spIds = serviceRequestService.getRandomServiceProviderIdsFromServiceRequestByStatus();
        if(spIds != null && !spIds.isEmpty()) {
        	ArrayList<Long> spIdArray = new ArrayList<Long>(spIds);
        	serviceRequestService.sendToKafka(spIdArray);
        }
        
    }

}
