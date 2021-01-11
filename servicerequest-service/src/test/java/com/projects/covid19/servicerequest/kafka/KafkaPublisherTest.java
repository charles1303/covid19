package com.projects.covid19.servicerequest.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.projects.covid19.servicerequest.kafka.producers.ServiceRequestKafkaPublisher;

@ExtendWith(SpringExtension.class)
@EmbeddedKafka(partitions = 1,controlledShutdown = true, brokerProperties={
        "log.dir=target/embedded-kafka"
})
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class KafkaPublisherTest {
	
	@Value("${kafka.bootstrap-servers}")
	private String bootstrapServers;
	
	@Value(value = "${kafka.service-request.groupId}")
    private String groupId;
	
	private String topic = "topic";
	
	@InjectMocks
	private ServiceRequestKafkaPublisher sender;
	
	@Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    public void testSendingKafkaEvents() {
        Consumer<String, ArrayList<Long>> consumer = configureConsumer();
        
        
        sender.setKafkaTemplate(kafkaTemplate());
        sender.setTopicName(topic);
        ArrayList<Long> message = new ArrayList<Long>(List.of(1L,2L,3L));
        sender.sendMessage(message);
        
        ConsumerRecords<String, ArrayList<Long>> singleRecords = KafkaTestUtils.getRecords(consumer);
        ConsumerRecord<String, ArrayList<Long>> singleRecord = singleRecords.iterator().next();
        assertThat(singleRecord).isNotNull();
        List<Long> records = singleRecord.value();
        assertTrue(records.size() == 3);
        assertTrue(records.contains(1));
        assertTrue(records.contains(2));
        assertTrue(records.contains(3));
        consumer.close();
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

    private Producer<String, List<Long>> configureProducer() {
        Map<String, Object> producerProps = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
       
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<String, List<Long>>(producerProps).createProducer();
    }
    
    public KafkaTemplate<String, ArrayList<Long>> kafkaTemplate() {
	      return new KafkaTemplate<>(configureProducerFactory());
	   }


}
