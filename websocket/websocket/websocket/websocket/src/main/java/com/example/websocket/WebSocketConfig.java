package com.example.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketFactory webSocketFactory;

    // Set to hold active UIDs
    private final Set<String> activeUids = new HashSet<>();

    @Autowired
    public WebSocketConfig(WebSocketFactory webSocketFactory) {
        this.webSocketFactory = webSocketFactory;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .withSockJS();

    // Register WebSocket endpoint with UID passed as a query parameter
    for (String uid : activeUids) {
        String endpoint = "/ws";  // Use a single endpoint for all UIDs
        registry.addEndpoint(endpoint + "?gameId=" + uid) // Pass UID as query parameter
                .setAllowedOriginPatterns("*")
                .withSockJS();
        System.out.println("Registered WebSocket endpoint: " + endpoint + "?gameId=" + uid);
    }
}


    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    // Method to dynamically create a WebSocket and add the UID to the active set
    public String createNewWebSocket(String type) {
        // Generate UID and register it
        String uid = webSocketFactory.createWebSocket(type);
        activeUids.add(uid);

        // After adding, we will ensure that the endpoints are re-registered when the WebSocketConfig is updated.
        return uid;
    }

    // Additional helper method to unregister a WebSocket based on UID
    public void removeWebSocket(String uid) {
        activeUids.remove(uid);
    }
}
