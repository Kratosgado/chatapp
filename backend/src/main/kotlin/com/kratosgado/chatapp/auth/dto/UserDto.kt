package com.kratosgado.chatapp.auth.dto

import com.kratosgado.chatapp.users.User

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String? = null,
) {
    companion object {
        fun fromEntity(user: User): UserDto =
            UserDto(
                id = user.id ?: "",
                name = user.name,
                email = user.email,
                avatarUrl = user.avatarUrl,
            )
    }
}
