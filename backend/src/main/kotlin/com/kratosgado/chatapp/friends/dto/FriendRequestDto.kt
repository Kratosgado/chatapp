package com.kratosgado.chatapp.friends.dto

import com.kratosgado.chatapp.auth.dto.UserDto
import java.time.LocalDateTime

data class FriendRequestDto(
    val id: String,
    val sender: UserDto,
    val createdAt: LocalDateTime,
)
