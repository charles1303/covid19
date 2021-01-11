package com.projects.covid19.notification.services;

import static org.mockito.BDDMockito.given;

import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.projects.covid19.notification.models.Notification;
import com.projects.covid19.notification.repositories.NotificationRepository;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class NotificationServiceTest {
	
	@InjectMocks
	NotificationService notificationService;
	
	@Mock
	NotificationRepository notificationRepository;
	
	@Test
	public void getAll() {
		given(this.notificationRepository.findAll())
				.willReturn(Flux.just(
						Notification.builder().name("notification1").description("notification-description").build(),
						Notification.builder().name("notification2").description("notification-description").build(),
						Notification.builder().name("notification3").description("notification-description").build()));
		Flux<Notification> notifications = notificationService.getAll();
		Predicate<Notification> match = notification -> notifications.any(saveItem -> saveItem.equals(notification)).block();
		StepVerifier
				.create(notifications)
				.expectNextMatches(match)
				.expectNextMatches(match)
				.expectNextMatches(match)
				.verifyComplete();
	}

}
