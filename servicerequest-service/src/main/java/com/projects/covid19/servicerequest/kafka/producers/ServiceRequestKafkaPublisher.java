package com.projects.covid19.servicerequest.kafka.producers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@Setter @Getter @Slf4j
public class ServiceRequestKafkaPublisher {
	
	@Value(value = "${kafka.service-request.topic.name}")
    private String topicName;
	
	@Autowired
	private KafkaTemplate<String, ArrayList<Long>> kafkaTemplate;
	
public void sendMessage(ArrayList<Long> message) {
        
	    ListenableFuture<SendResult<String, ArrayList<Long>>> future = 
	      kafkaTemplate.send(topicName, message);
	     
	    future.addCallback(new ListenableFutureCallback<SendResult<String, ArrayList<Long>>>() {
	 
	        @Override
	        public void onSuccess(SendResult<String, ArrayList<Long>> result) {
	            log.debug("Sent message=[ {} ] with offset=[ {} ]" ,message, result.getRecordMetadata().offset());
	        }
	        @Override
	        public void onFailure(Throwable ex) {
	        	log.debug("Unable to send message=[ {} ] due to {}", message, ex.getMessage());
	        }
	    });
	}

}
