package com.projects.covid19.notification.handlers;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;

import com.projects.covid19.notification.config.WebFluxConfig;
import com.projects.covid19.notification.exceptions.GeneralException;
import com.projects.covid19.notification.exceptions.GlobalErrorWebExceptionHandler;
import com.projects.covid19.notification.models.Notification;
import com.projects.covid19.notification.repositories.NotificationRepository;
import com.projects.covid19.notification.services.NotificationService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest
@Import({NotificationService.class, NotificationHandler.class,WebFluxConfig.class})
@WithMockUser(roles = { "ADMIN", "USER" })
public class NotificationHandlerTest {

	@MockBean
	NotificationRepository repository;

	@Autowired
	private WebTestClient webClient;
	
	@Autowired
	WebFluxConfig config;
	
	@Mock
	NotificationHandler notificationHandler;
	
		
	@Test
	void testCreateNotification() {
		webClient = WebTestClient
		      .bindToRouterFunction(config.routeNotification(notificationHandler))
		      .build();
		 
		   
		
		Notification notification = new Notification("name", "description");
		notification.setId("1");

		Mockito.when(repository.save(notification)).thenReturn(Mono.just(notification));

		webClient.mutateWith(csrf()).post().uri("/api/handler/notification").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).body(BodyInserters.fromObject(notification)).exchange()
				.expectStatus().is2xxSuccessful();

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

		webClient.get().uri("/api/handler/notification").header(HttpHeaders.ACCEPT, "application/json").exchange()
				.expectStatus().isOk().expectBodyList(Notification.class);

		Mockito.verify(repository, times(1)).findAll();

	}

	@Test
	void testGetOneNotification() {
		Notification notification = new Notification("name", "description");
		notification.setId("1");

		Mockito.when(repository.findById("1")).thenReturn(Mono.just(notification));

		webClient.get().uri("/api/handler/notification/{id}", "1").exchange().expectStatus().isOk().expectBody()
				.jsonPath("$.name").isNotEmpty().jsonPath("$.id").isEqualTo("1").jsonPath("$.name").isEqualTo("name")
				.jsonPath("$.description").isEqualTo("description");

		Mockito.verify(repository, times(1)).findById("1");
	}
	
	@Test
	void testNotFoundResponse() {
		webClient = WebTestClient
		      .bindToRouterFunction(config.routeNotification(notificationHandler))
		      .build();
		String id = "1";
		Mockito.when(repository.findById(any(String.class))).thenReturn(Mono.empty());
		
		webClient.get().uri("/api/handler/notification/findbyid/{id}", "1").exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isNotFound();

		Mockito.verify(repository, times(1)).findById(id);
	}
	
	@Test
	void testException() throws GeneralException {
		webClient = WebTestClient
		      .bindToRouterFunction(config.routeNotification(notificationHandler))
		      .build();
		Notification notification = new Notification("name", "description");
		notification.setId("1");

		Mockito.when(repository.save(notification)).thenThrow(new IllegalArgumentException());
		
		webClient.mutateWith(csrf()).post().uri("/api/handler/notification/dosave").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).body(BodyInserters.fromObject(notification)).exchange()
				.expectStatus().is5xxServerError()
				.expectBody(GeneralException.class);

		Mockito.verify(repository, times(1)).save(notification);
	}


	private void chec() {
		Notification notification = new Notification("name", "description");
		notification.setId("1");
		WebClient client = WebClient.create("http://localhost:8080");

		Mono<Notification> notificationMono = client.get().uri("/api/handler/notification/{id}", "1")
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(Notification.class);

		notificationMono.map(m -> m.getName()).subscribe(System.out::println);

		Flux<Notification> notificationFlux = client.get().uri("/api/handler/notification").accept(MediaType.APPLICATION_JSON)
				.retrieve().bodyToFlux(Notification.class);

		notificationFlux.map(m -> m.getName()).subscribe(System.out::println);

		client.post().uri("/api/handler/notification").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).body(BodyInserters.fromObject(notification)).retrieve()
				.bodyToMono(Notification.class);
	}
}
