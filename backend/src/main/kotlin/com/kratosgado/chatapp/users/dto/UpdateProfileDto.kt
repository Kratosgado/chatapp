package com.kratosgado.chatapp.users.dto

data class UpdateProfileDto(
    val name: String,
    val avatarUrl: String? = null,
)
