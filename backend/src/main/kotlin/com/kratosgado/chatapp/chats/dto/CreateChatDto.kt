package com.kratosgado.chatapp.chats.dto

import jakarta.validation.constraints.NotEmpty

data class CreateChatDto(
    val name: String? = null,
    @field:NotEmpty
    val participantIds: List<String>,
)
