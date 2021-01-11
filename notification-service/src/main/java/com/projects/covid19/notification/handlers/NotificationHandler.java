package com.projects.covid19.notification.handlers;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.projects.covid19.notification.exceptions.GeneralException;
import com.projects.covid19.notification.models.Notification;
import com.projects.covid19.notification.repositories.NotificationRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class NotificationHandler {
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	
	public Mono<ServerResponse> getAll(ServerRequest request) {
		return ServerResponse.ok().body(notificationRepository.findAll().switchIfEmpty(Flux.empty()), Notification.class);
	}
	
	public Mono<ServerResponse> getOne(ServerRequest request) {
		 return ServerResponse.ok().body(notificationRepository.findById(request.pathVariable("id")), Notification.class)
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> findById(ServerRequest request) {
		Mono<Notification> monoNotification = notificationRepository.findById(request.pathVariable("id"));
		if(monoNotification.block() == Mono.empty().block()) {
			return ServerResponse.notFound().build();
		}
		 return ServerResponse.ok().body(monoNotification, Notification.class);
	}
	
	public Mono<ServerResponse> saveOne(ServerRequest request) {
		Mono<Notification> requestBodyMono = request.body(BodyExtractors.toMono(Notification.class));
		Notification notification =  requestBodyMono.block();
		return ServerResponse.ok().body(fromObject(notificationRepository.save(notification)))
				.onErrorResume(e -> Mono.error(new GeneralException(
				        HttpStatus.INTERNAL_SERVER_ERROR, 
				        "Error on server!", e)));
	}
	
	public Mono<ServerResponse> doSave(ServerRequest request){
		Mono<Notification> requestBodyMono = request.body(BodyExtractors.toMono(Notification.class));
		Notification notification =  requestBodyMono.block();
		return ServerResponse.ok().body(fromObject(notificationRepository.save(notification)))
				.onErrorReturn(ServerResponse.badRequest().body(fromObject(new GeneralException(
				        HttpStatus.BAD_REQUEST, 
				        "Bad Parameters!"))).block());
	}
	
	
	public Mono<ServerResponse> updateOne(ServerRequest request) {
		Mono<Notification> requestBodyMono = request.body(BodyExtractors.toMono(Notification.class));
		Notification notification =  requestBodyMono.block();
		notification.setId(request.pathVariable("id"));
		return ServerResponse.ok().body(fromObject(notificationRepository.save(notification)))
				.onErrorReturn(ServerResponse.badRequest().body(fromObject(new GeneralException(
				        HttpStatus.BAD_REQUEST, 
				        "Bad Parameters!"))).block());
	}
	
	public Mono<ServerResponse> delete(ServerRequest request) {
		final Mono<Notification> dbNotification = notificationRepository.findById(request.pathVariable("id"));
		if (Objects.isNull(dbNotification)) {
			return Mono.empty();
		}
		return ServerResponse.ok().body(fromObject(dbNotification.switchIfEmpty(Mono.empty()).filter(Objects::nonNull).flatMap(notificationToBeDeleted -> notificationRepository
				.delete(notificationToBeDeleted).then(Mono.just(notificationToBeDeleted)))))
				.onErrorReturn(ServerResponse.badRequest().body(fromObject(new GeneralException(
				        HttpStatus.BAD_REQUEST, 
				        "Bad Parameters!"))).block());
	}

}
