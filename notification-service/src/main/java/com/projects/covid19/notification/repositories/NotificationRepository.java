package com.projects.covid19.notification.repositories;

import org.reactivestreams.Publisher;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.projects.covid19.notification.models.Notification;

import reactor.core.publisher.Mono;

@Repository
public interface NotificationRepository extends ReactiveMongoRepository<Notification, String> {

	Publisher<Void> saveMono(Mono<Notification> bodyToMono);

}
