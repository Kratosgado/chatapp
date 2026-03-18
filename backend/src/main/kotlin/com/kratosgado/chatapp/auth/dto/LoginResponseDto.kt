package com.kratosgado.chatapp.auth.dto

data class LoginResponseDto(
    val token: String,
    val user: UserDto
)
