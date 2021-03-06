package com.projects.covid19.servicerequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages={"com.projects.covid19.servicerequest"})
@EnableScheduling
@EnableAsync
@EnableKafka
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
        
    }
    
}
