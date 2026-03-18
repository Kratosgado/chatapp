package com.kratosgado.chatapp.configs

import com.kratosgado.chatapp.services.JwtService
import com.kratosgado.chatapp.users.UserRepo
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userRepo: UserRepo,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val cookies = request.cookies
        val jwtCookie = cookies?.find { it.name == "jwt" }
        val token = jwtCookie?.value

        if (token != null && jwtService.validateToken(token)) {
            val userId = jwtService.getUserIdFromToken(token)
            if (userId != null && SecurityContextHolder.getContext().authentication == null) {
                val user = userRepo.findById(userId).orElse(null)
                if (user != null) {
                    val authToken = UsernamePasswordAuthenticationToken(user, null, emptyList())
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }
        }

        filterChain.doFilter(request, response)
    }
}
