package com.proliferate.Proliferate.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class ServerWebSocketConfig implements WebSocketConfigurer {
    @Autowired
    private final WebSocketHandler webSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(videoCallWebSocketHandler(), "/ws/call")
                .setAllowedOrigins("https://proliferate-2.onrender.com")
				.withSockJS(); // Add SockJS support if needed for fallback
    }

    @Bean
    public VideoCallWebSocketHandler videoCallWebSocketHandler(){
        return new VideoCallWebSocketHandler();
    }

}
