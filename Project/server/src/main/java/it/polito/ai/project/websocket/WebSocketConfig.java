package it.polito.ai.project.websocket;

import it.polito.ai.project.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;
import java.util.List;
import java.util.Map;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    boolean tokenIsPresent = false;
                    MultiValueMap<String, String> multiValueMap = message.getHeaders().get(StompHeaderAccessor.NATIVE_HEADERS, MultiValueMap.class);
                    for (Map.Entry<String, List<String>> entry1 : multiValueMap.entrySet()) {
                        String name = entry1.getKey();
                        if (name.equals("Authorization")) {
                            String jwt = entry1.getValue().get(0);
                            if (jwt != null && jwt.startsWith("Bearer ") && jwt.length() > 7) {
                                Authentication user = jwtTokenProvider.getAuthentication(jwt.substring(7));
                                accessor.setUser(user);
                                SecurityContextHolder.getContext().setAuthentication(user);
                                tokenIsPresent = true;
                            }
                            break;
                        }
                    }
                    if (!tokenIsPresent) SecurityContextHolder.getContext().setAuthentication(null);
                }
                return message;
            }
        });
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
    }
}