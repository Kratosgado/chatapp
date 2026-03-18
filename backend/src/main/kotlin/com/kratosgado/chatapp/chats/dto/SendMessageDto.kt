package com.kratosgado.chatapp.chats.dto

import jakarta.validation.constraints.NotEmpty

data class SendMessageDto(
    @field:NotEmpty
    val content: String,
)
