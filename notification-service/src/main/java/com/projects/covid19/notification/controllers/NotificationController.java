package com.projects.covid19.notification.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.projects.covid19.notification.models.Notification;
import com.projects.covid19.notification.services.NotificationService;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/notification")
@AllArgsConstructor
public class NotificationController {
	
	private final NotificationService notificationService;
	
	@GetMapping
	public Flux<Notification> getAll() {
		return notificationService.getAll();
	}
	
	@GetMapping("/{id}")
	public Mono<Notification> getById(@PathVariable("id") final String id) {
		return notificationService.getById(id);
	}
	
	@PutMapping("/{id}")
	public Mono<Notification> updateById(@PathVariable("id") final String id, @RequestBody final Notification notification) {
		return notificationService.update(id, notification);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Notification> save(@RequestBody final Notification notification) {
		return notificationService.save(notification);
	}
	
	@DeleteMapping("/{id}")
	public Mono<Notification> delete(@PathVariable final String id) {
		return notificationService.delete(id);
	}

}
