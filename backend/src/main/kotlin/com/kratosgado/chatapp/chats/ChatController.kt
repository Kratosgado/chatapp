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
        @AuthenticationPrincipal user: User,
        @RequestBody @Valid dto: CreateChatDto,
    ): ChatRoomDto {
        return chatService.createChat(user.id!!, dto.name, dto.participantIds)
    }

    @GetMapping
    fun getChats(
        @AuthenticationPrincipal user: User,
    ): List<ChatRoomDto> {
        return chatService.getChats(user.id!!)
    }

    @GetMapping("/{chatId}/messages")
    fun getMessages(
        @AuthenticationPrincipal user: User,
        @PathVariable chatId: String,
        @PageableDefault(size = 20) pageable: Pageable,
    ): List<MessageDto> {
        return chatService.getMessages(user.id!!, chatId, pageable)
    }

    @PostMapping("/{chatId}/messages")
    fun sendMessage(
        @AuthenticationPrincipal user: User,
        @PathVariable chatId: String,
        @RequestBody @Valid dto: SendMessageDto,
    ): MessageDto {
        return chatService.sendMessage(user.id!!, chatId, dto.content)
    }
}
