package com.kratosgado.chatapp.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record LoginDto(
    @Email String email,
    @NotBlank @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters") String password) {
}
