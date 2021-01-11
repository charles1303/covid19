package com.projects.covid19.serviceprovider.kafka.consumers;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component @Slf4j
public class ServiceRequestKafkaConsumer {
	
	private CountDownLatch latch = new CountDownLatch(1);

	  public CountDownLatch getLatch() {
	    return latch;
	  }
	
	@KafkaListener(topics = {"service-requests"}, groupId = "providers")
	public void listen(List<Long> message) {
		log.debug("Received Messasge in group log: {}", message);
	    latch.countDown();
	}


}
