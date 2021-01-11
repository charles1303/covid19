package com.projects.covid19.serviceprovider.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.projects.covid19.serviceprovider.kafka.consumers.ServiceRequestKafkaConsumer;


@ExtendWith(SpringExtension.class)
//@SpringJUnitConfig
//@DirtiesContext
//@SpringBootTest(classes= TestConfig.class)
@EmbeddedKafka(partitions = 1,topics = { "service-requests" },controlledShutdown = true, brokerProperties={
        "log.dir=target/embedded-kafka"
})
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class KafkaConsumerTest {
	
	@Value("${kafka.bootstrap-servers}")
	private String bootstrapServers;
	
	@Value(value = "${kafka.service.provider.groupId}")
    private String groupId;
	
	@Value(value = "${kafka.service.provider.topic.name}")
    private String topic;
	
	@Autowired
	private ServiceRequestKafkaConsumer consumer;
	
	//@Autowired
	//KafkaTemplate<String, ArrayList<Long>> kafkaTemplate = kafkaTemplate();

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;
    
    @Configuration
    @ComponentScan({ "com.projects.covid19.serviceprovider.kafka" })
    static class TestConfig {
    	
    	@Bean
        public ServiceRequestKafkaConsumer consumer() {
            return new ServiceRequestKafkaConsumer();
        }
    }
    
    @Test
    public void testReceivingKafkaEvents() throws InterruptedException {
        
    	Producer<String, ArrayList<Long>> producer = configureProducer();
    	
    	
        producer.send(new ProducerRecord<>(topic, "123", new ArrayList<Long>(List.of(1L,2L,3L))));
    	//kafkaTemplate().send(new ProducerRecord<>(topic, null, new ArrayList<Long>(List.of(1L,2L,3L))));
        consumer.getLatch().await(10000, TimeUnit.MILLISECONDS);
        //assertThat(consumer.getLatch().getCount()).isEqualTo(0);   
        //assertThat(consumer.getLatch().await(10L, TimeUnit.SECONDS)).isTrue();
        
    }
        
    
    private Consumer<String, ArrayList<Long>> configureConsumer() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(groupId, "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(
          ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, 
          bootstrapServers);
        consumerProps.put(
          ConsumerConfig.GROUP_ID_CONFIG, 
          groupId);
        consumerProps.put(
          ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, 
          StringDeserializer.class);
        consumerProps.put(
        		ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, 
        	      new JsonDeserializer<>(ArrayList.class));
        Consumer<String, ArrayList<Long>> consumer = new DefaultKafkaConsumerFactory<String, ArrayList<Long>>(consumerProps,new StringDeserializer(),
                new JsonDeserializer<>(ArrayList.class))
                .createConsumer();
        consumer.subscribe(Collections.singleton(topic));
        return consumer;
    }
    
    private ProducerFactory<String, ArrayList<Long>> configureProducerFactory() {
        Map<String, Object> producerProps = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
       
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<String, ArrayList<Long>>(producerProps);
    }

    private Producer<String, ArrayList<Long>> configureProducer() {
        Map<String, Object> producerProps = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
       
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<String, ArrayList<Long>>(producerProps).createProducer();
    }
    
    public KafkaTemplate<String, ArrayList<Long>> kafkaTemplate() {
    	KafkaTemplate<String, ArrayList<Long>> temp  = new KafkaTemplate<>(configureProducerFactory());
    	temp.setDefaultTopic(topic);
    	return temp;
    	//return new KafkaTemplate<>(configureProducerFactory());
	   }


}
