package com.digitalspace.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/*
 * This class configures WebSocket for our application.
 *
 * Think of WebSocket as a phone call between:
 *
 * React  <----------------->  Spring Boot
 *
 * Unlike normal HTTP requests, the connection stays open.
 */
@Configuration

/*
 * Enables WebSocket message handling in Spring Boot.
 */
@EnableWebSocketMessageBroker
public class WebSocketConfig
        implements WebSocketMessageBrokerConfigurer {

    /*
     * Configure how messages move around.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        /*
         * "/topic" is where Spring sends messages
         * to connected users.
         *
         * Example:
         *
         * /topic/room/ABC123
         *
         * means:
         * "Send this message to everyone
         * connected to room ABC123."
         */
        config.enableSimpleBroker("/topic");

        /*
         * "/app" is used when React sends
         * a message TO our Spring Boot backend.
         *
         * Example:
         *
         * /app/status
         */
        config.setApplicationDestinationPrefixes("/app");
    }

    /*
     * This creates the endpoint where React
     * initially connects to WebSocket.
     */
    @Override
    public void registerStompEndpoints(
            StompEndpointRegistry registry) {

        /*
         * React will connect to:
         *
         * http://localhost:8080/ws
         *
         * This is NOT a normal REST API.
         * It is the WebSocket connection endpoint.
         */
        registry.addEndpoint("/ws")

                /*
                 * Allow our React development server
                 * to connect.
                 */
                .setAllowedOriginPatterns(
                        "http://localhost:5173"
                );
    }
}