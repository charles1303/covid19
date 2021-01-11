package com.projects.covid19.notification.repo.controllers;

import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.projects.covid19.notification.controllers.NotificationController;
import com.projects.covid19.notification.handlers.NotificationHandler;
import com.projects.covid19.notification.models.Notification;
import com.projects.covid19.notification.repositories.NotificationRepository;
import com.projects.covid19.notification.services.NotificationService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = NotificationController.class)
@Import({NotificationService.class, NotificationHandler.class})
@WithMockUser(roles = { "ADMIN", "USER" })
public class NotificationControllerTest {

	@MockBean
	NotificationRepository repository;

	@Autowired
	private WebTestClient webClient;
	
	@Test
	void testCreateNotification() {
		Notification notification = new Notification("name", "description");
		notification.setId("1");

		Mockito.when(repository.save(notification)).thenReturn(Mono.just(notification));

		webClient.mutateWith(csrf()).post().uri("/api/notification").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).body(BodyInserters.fromObject(notification)).exchange()
				//.expectStatus().is2xxSuccessful();
				.expectStatus().isCreated();

		Mockito.verify(repository, times(1)).save(notification);
	}

	@Test
	void testGetAllNotifications() {
		Notification notification = new Notification("name", "description");
		notification.setId("1");

		List<Notification> list = new ArrayList<Notification>();
		list.add(notification);

		Notification notification2 = new Notification("name2", "description2");
		notification2.setId("2");
		list.add(notification2);

		Flux<Notification> notificationFlux = Flux.fromIterable(list);

		Mockito.when(repository.findAll()).thenReturn(notificationFlux);

		webClient.get().uri("/api/notification").header(HttpHeaders.ACCEPT, "application/json").exchange()
				.expectStatus().isOk().expectBodyList(Notification.class);

		Mockito.verify(repository, times(1)).findAll();

	}

	@Test
	void testGetOneNotification() {
		Notification notification = new Notification("name", "description");
		notification.setId("1");

		Mockito.when(repository.findById("1")).thenReturn(Mono.just(notification));

		// webClient.get().uri("/api/notification/1")
		webClient.get().uri("/api/notification/{id}", "1").exchange().expectStatus().isOk().expectBody()
				.jsonPath("$.name").isNotEmpty().jsonPath("$.id").isEqualTo("1").jsonPath("$.name").isEqualTo("name")
				.jsonPath("$.description").isEqualTo("description");

		Mockito.verify(repository, times(1)).findById("1");
	}

	private void chec() {
		Notification notification = new Notification("name", "description");
		notification.setId("1");
		WebClient client = WebClient.create("http://localhost:8080");

		Mono<Notification> notificationMono = client.get().uri("/api/notification/{id}", "1")
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(Notification.class);

		notificationMono.map(m -> m.getName()).subscribe(System.out::println);

		Flux<Notification> notificationFlux = client.get().uri("/api/notification").accept(MediaType.APPLICATION_JSON)
				.retrieve().bodyToFlux(Notification.class);

		notificationFlux.map(m -> m.getName()).subscribe(System.out::println);

		client.post().uri("/api/notification").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).body(BodyInserters.fromObject(notification)).retrieve()
				.bodyToMono(Notification.class);
	}
}
