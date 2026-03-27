package com.kratosgado.chatapp.chats

import com.kratosgado.chatapp.chats.dto.ChatRoomDto
import com.kratosgado.chatapp.chats.dto.CreateChatDto
import com.kratosgado.chatapp.chats.dto.MessageDto
import com.kratosgado.chatapp.chats.dto.SendMessageDto
import com.kratosgado.chatapp.users.User
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/chats")
class ChatController(
    private val chatService: ChatService,
) {
    @PostMapping
    fun createChat(
        @AuthenticationPrincipal userId: String,
        @RequestBody @Valid dto: CreateChatDto,
    ): ChatRoomDto = chatService.createChat(userId, dto.name, dto.participantIds)

    @GetMapping
    fun getChats(
        @AuthenticationPrincipal userId: String,
    ): List<ChatRoomDto> = chatService.getChats(userId)

    @GetMapping("/{chatId}/messages")
    fun getMessages(
        @AuthenticationPrincipal userId: String,
        @PathVariable chatId: String,
        @PageableDefault(size = 20) pageable: Pageable,
    ): List<MessageDto> = chatService.getMessages(userId, chatId, pageable)

    @PostMapping("/{chatId}/messages")
    fun sendMessage(
        @AuthenticationPrincipal userId: String,
        @PathVariable chatId: String,
        @RequestBody @Valid dto: SendMessageDto,
    ): MessageDto = chatService.sendMessage(userId, chatId, dto.content)

    @DeleteMapping("/{chatId}")
    fun deleteChat(
        @AuthenticationPrincipal userId: String,
        @PathVariable chatId: String,
    ): String = chatService.deleteChat(userId, chatId)
}
