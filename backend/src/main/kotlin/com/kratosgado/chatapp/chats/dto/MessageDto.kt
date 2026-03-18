package com.kratosgado.chatapp.chats.dto

import com.kratosgado.chatapp.auth.dto.UserDto
import com.kratosgado.chatapp.chats.Message
import java.time.LocalDateTime

data class MessageDto(
    val id: String,
    val content: String,
    val sender: UserDto,
    val sentAt: LocalDateTime,
) {
    companion object {
        fun fromEntity(message: Message): MessageDto {
            return MessageDto(
                id = message.id!!,
                content = message.content,
                sender = UserDto.fromEntity(message.sender),
                sentAt = message.sentAt,
            )
        }
    }
}
