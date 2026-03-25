package com.kratosgado.chatapp.services

import com.kratosgado.chatapp.users.User
import com.kratosgado.chatapp.utils.UtilConstants
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${security.jwt.secret-key}") private val secretKey: String,
) {
    private fun getSigningKey(): SecretKey {
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
            .subject(user.id)
            .claim("email", user.email)
            .claim("name", user.name)
            .expiration(Date.from(Instant.now().plusSeconds(UtilConstants.ONE_DAY_IN_SECONDS.toLong())))
            .signWith(getSigningKey())
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            parseToken(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun parseToken(token: String) = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token)

    fun getUserIdFromToken(token: String): String? {
        return try {
            parseToken(token).payload.subject
        } catch (e: Exception) {
            null
        }
    }
}
