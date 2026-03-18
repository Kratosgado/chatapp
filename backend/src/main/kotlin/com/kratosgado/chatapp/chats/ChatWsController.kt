package com.kratosgado.chatapp.chats

import com.kratosgado.chatapp.chats.dto.SendMessageDto
import com.kratosgado.chatapp.users.User
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Controller
import java.security.Principal

data class TypingDto(
    val isTyping: Boolean,
)

@Controller
class ChatWsController(
    private val chatService: ChatService,
    private val messagingTemplate: SimpMessagingTemplate,
) {
    @MessageMapping("/chat.sendMessage/{chatId}")
    fun sendMessage(
        @DestinationVariable chatId: String,
        @Payload request: SendMessageDto,
        principal: Principal,
    ) {
        val user = (principal as UsernamePasswordAuthenticationToken).principal as User
        val message = chatService.sendMessage(user.id!!, chatId, request.content)
        messagingTemplate.convertAndSend("/topic/chats/$chatId", message)
    }

    @MessageMapping("/chat.typing/{chatId}")
    fun sendTyping(
        @DestinationVariable chatId: String,
        @Payload request: TypingDto,
        principal: Principal,
    ) {
        val user = (principal as UsernamePasswordAuthenticationToken).principal as User
        val payload = mapOf("userId" to user.id, "isTyping" to request.isTyping)
        messagingTemplate.convertAndSend("/topic/chats/$chatId/typing", payload)
    }
}
