package com.projects.covid19.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.projects.covid19.notification.exceptions.GeneralException;
import com.projects.covid19.notification.handlers.NotificationHandler;

import reactor.core.publisher.Mono;

@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {
	
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            //.allowedOrigins("*")
            .allowedMethods("GET", "PUT", "DELETE")
            //.allowedHeaders("testHeader")
            .allowCredentials(true);
    }
	
	@Bean
    public RouterFunction<ServerResponse> routeNotification(NotificationHandler notificationHandler) {
        return RouterFunctions.route(RequestPredicates.GET("/api/handler/notification").
        		and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), notificationHandler::getAll)
        		
        		.andRoute(RequestPredicates.GET("/api/handler/notification/{id}").
        				and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), notificationHandler::getOne)
        		
        		.andRoute(RequestPredicates.GET("/api/handler/notification/findbyid/{id}").
        				and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), notificationHandler::findById)
        		
        		.andRoute(RequestPredicates.POST("/api/handler/notification").
        				and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), notificationHandler::saveOne)
        		
        		
        		.andRoute(RequestPredicates.POST("/api/handler/notification/dosave").
        				and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), notificationHandler::doSave)
        		
        			
        		.andRoute(RequestPredicates.PUT("/api/handler/notification/{id}").
        				and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), 
        				notificationHandler::updateOne)
        		
        		.andRoute(RequestPredicates.DELETE("/api/handler/notification/{id}")
        	             .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), notificationHandler::delete);
    }

}
