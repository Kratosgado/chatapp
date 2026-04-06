package com.kratosgado.chatapp.configs

import com.kratosgado.chatapp.services.JwtService
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    private val jwtService: JwtService,
) : WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/topic", "/queue")
        config.setApplicationDestinationPrefixes("/app")
        config.setUserDestinationPrefix("/user")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry
            .addEndpoint("/ws")
            .setAllowedOrigins(
                "http://localhost:3000", "https://gachat-67v.pages.dev", "http://localhost:5173"
            )
            .withSockJS()
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(
            object : ChannelInterceptor {
                override fun preSend(
                    message: Message<*>,
                    channel: MessageChannel,
                ): Message<*>? {
                    val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

                    if (StompCommand.CONNECT == accessor?.command) {
                        val authHeader = accessor.getFirstNativeHeader("Authorization")
                        if (authHeader != null && authHeader.startsWith("Bearer ")) {
                            val token = authHeader.substring(7)
                            if (jwtService.validateToken(token)) {
                                val userId = jwtService.getUserIdFromToken(token)
                                if (userId != null) {
                                    val auth =
                                        UsernamePasswordAuthenticationToken(
                                            userId,
                                            null,
                                            emptyList(),
                                        )
                                    accessor.user = auth
                                }
                            }
                        }
                    }
                    return message
                }
            },
        )
    }
}
