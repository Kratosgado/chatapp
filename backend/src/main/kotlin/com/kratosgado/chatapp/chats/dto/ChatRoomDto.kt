package com.kratosgado.chatapp.chats.dto

import com.kratosgado.chatapp.auth.dto.UserDto
import org.springframework.beans.factory.annotation.Value
import java.time.LocalDateTime

data class ChatRoomDto(
    val id: String,
    val name: String,
    val isGroup: Boolean,
    val avatarUrl: String? = null,
    val lastMessage: MessageDto? = null,
    val members: List<UserDto>,
    val createdAt: LocalDateTime,
)
