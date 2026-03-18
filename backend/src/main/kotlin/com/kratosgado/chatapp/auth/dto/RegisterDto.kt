package com.kratosgado.chatapp.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class RegisterDto(
    @field:NotEmpty
    val name: String,
    @field:Email
    val email: String,
    @field:NotBlank
    @field:Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    val password: String,
    val confirmPassword: String? = null,
)
