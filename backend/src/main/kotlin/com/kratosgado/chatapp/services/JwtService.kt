package com.kratosgado.chatapp.services

import com.kratosgado.chatapp.users.User
import com.kratosgado.chatapp.utils.UtilConstants
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Date

@Service
class JwtService(
    @Value("\${security.jwt.secret-key}") private val secretKey: String,
) {
    private fun getSigningKey(): java.security.Key {
        val keyBytes = secretKey.toByteArray()
        return try {
            Keys.hmacShaKeyFor(keyBytes)
        } catch (e: Exception) {
            throw RuntimeException("Invalid JWT secret key", e)
        }
    }

    fun generateToken(user: User): String {
        return Jwts
            .builder()
            .setSubject(user.id)
            .claim("email", user.email)
            .claim("name", user.name)
            .setExpiration(Date.from(Instant.now().plusSeconds(UtilConstants.ONE_DAY_IN_SECONDS.toLong())))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUserIdFromToken(token: String): String? {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .body
            claims.subject
        } catch (e: Exception) {
            null
        }
    }
}
