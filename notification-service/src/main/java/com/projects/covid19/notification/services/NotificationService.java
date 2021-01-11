package com.projects.covid19.notification.services;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projects.covid19.notification.models.Notification;
import com.projects.covid19.notification.repositories.NotificationRepository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Transactional
@AllArgsConstructor
public class NotificationService {
	private final NotificationRepository notificationRepository;
	
	public Flux<Notification> getAll() {
		return notificationRepository.findAll().switchIfEmpty(Flux.empty());
	}
	
	public Mono<Notification> getById(final String id) {
		return notificationRepository.findById(id);
	}
	
	public Mono<Notification> update(final String id, final Notification notification) {
		return notificationRepository.save(notification);
	}
	
	public Mono<Notification> save(final Notification notification) {
		return notificationRepository.save(notification);
	}
	
	public Mono<Notification> delete(final String id) {
		final Mono<Notification> dbNotification = getById(id);
		if (Objects.isNull(dbNotification)) {
			return Mono.empty();
		}
		return getById(id).switchIfEmpty(Mono.empty()).filter(Objects::nonNull).flatMap(notificationToBeDeleted -> notificationRepository
				.delete(notificationToBeDeleted).then(Mono.just(notificationToBeDeleted)));
	}
}
