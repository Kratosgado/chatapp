package com.kratosgado.chatapp.auth

import com.kratosgado.chatapp.auth.dto.LoginDto
import com.kratosgado.chatapp.auth.dto.LoginResponseDto
import com.kratosgado.chatapp.auth.dto.RegisterDto
import com.kratosgado.chatapp.users.User
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {
    private val logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/login")
    fun login(
        @RequestBody @Valid dto: LoginDto,
        res: HttpServletResponse,
    ): LoginResponseDto {
        logger.info("logging in user {}", dto.email)
        return authService.login(dto, res)
    }

    @PostMapping("/logout")
    fun logout(res: HttpServletResponse) {
        logger.info("logging out user")
        authService.logout(res)
    }

    @PostMapping("/register")
    fun register(
        @RequestBody @Valid dto: RegisterDto,
        res: HttpServletResponse,
    ): User {
        logger.info("registering user {}", dto)
        return authService.register(dto, res)
    }
}
