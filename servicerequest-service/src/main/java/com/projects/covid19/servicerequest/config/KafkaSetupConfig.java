package com.projects.covid19.servicerequest.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaSetupConfig {
	
	@Value("${kafka.bootstrap-servers}")
	private String bootstrapServers;
	
	@Value(value = "${kafka.service-request.groupId}")
    private String groupId;
	
	@Bean
	   public ProducerFactory<String, ArrayList<Long>> producerFactory() {
	      Map<String, Object> configProps = new HashMap<>();
	      configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
	      configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	      configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
	      return new DefaultKafkaProducerFactory<>(configProps);
	   }
	   @Bean
	   public KafkaTemplate<String, ArrayList<Long>> kafkaTemplate() {
	      return new KafkaTemplate<>(producerFactory());
	   }
	   
	   @Bean
	    public ConsumerFactory<String, ArrayList<Long>> consumerFactory() {
	        Map<String, Object> props = new HashMap<>();
	        props.put(
	          ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, 
	          bootstrapServers);
	        props.put(
	          ConsumerConfig.GROUP_ID_CONFIG, 
	          groupId);
	        props.put(
	          ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, 
	          StringDeserializer.class);
	        props.put(
	        		ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, 
	        	      new JsonDeserializer<>(ArrayList.class));
	        return new DefaultKafkaConsumerFactory<>(props,new StringDeserializer(),
	                new JsonDeserializer<>(ArrayList.class));
	    }
	 
	    @Bean
	    public ConcurrentKafkaListenerContainerFactory<String, ArrayList<Long>> 
	      kafkaListenerContainerFactory() {
	    
	        ConcurrentKafkaListenerContainerFactory<String, ArrayList<Long>> factory =
	          new ConcurrentKafkaListenerContainerFactory<>();
	        factory.setConsumerFactory(consumerFactory());
	        return factory;
	    }
	    
	    @Bean
	    public ConcurrentKafkaListenerContainerFactory<String, ArrayList<Long>>
	      filterKafkaListenerContainerFactory() {
	     
	        ConcurrentKafkaListenerContainerFactory<String, ArrayList<Long>> factory =
	          new ConcurrentKafkaListenerContainerFactory<>();
	        factory.setConsumerFactory(consumerFactory());
	        factory.setRecordFilterStrategy(
	          record -> record.value() != null);
	        return factory;
	    }
	

}
