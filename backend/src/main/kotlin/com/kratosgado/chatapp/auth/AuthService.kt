package com.kratosgado.chatapp.auth

import com.kratosgado.chatapp.auth.dto.RegisterDto
import com.kratosgado.chatapp.services.JwtService
import com.kratosgado.chatapp.users.User
import com.kratosgado.chatapp.users.UserRepo
import com.kratosgado.chatapp.utils.ApiException
import com.kratosgado.chatapp.utils.UtilConstants
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepo: UserRepo,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
) {
    fun register(
        dto: RegisterDto,
        res: HttpServletResponse,
    ): User {
        if (userRepo.existsByEmail(dto.email)) {
            throw ApiException.badRequest("Email already registered")
        }

        if (dto.password != dto.confirmPassword) {
            throw ApiException.badRequest("Passwords do not match")
        }

        val user = User()
        user.name = dto.name
        user.email = dto.email
        user.password = passwordEncoder.encode(dto.password)
        userRepo.save(user)

        val token = jwtService.generateToken(user)
        val cookie = Cookie("jwt", token)

        cookie.isHttpOnly = true
        cookie.secure = true
        cookie.path = "/"
        cookie.maxAge = UtilConstants.ONE_DAY_IN_SECONDS
        res.addCookie(cookie)

        return user
    }
}
