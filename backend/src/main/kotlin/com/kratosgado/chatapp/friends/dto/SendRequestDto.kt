package com.kratosgado.chatapp.friends.dto

import jakarta.validation.constraints.NotBlank

data class SendRequestDto(
    @field:NotBlank
    val receiverId: String
)
