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
    fun generateToken(user: User): String {
        // Ensure the key is valid for HS256 (needs to be at least 32 bytes)
        // For simplicity in this migration, we assume the provided key is sufficient
        // or we pad/hash it if needed, but here we strictly follow the logic.
        // If the key is too short, Keys.hmacShaKeyFor might throw.
        // A common workaround for legacy keys is to use the bytes directly if supported,
        // but 0.11.x is strict.
        // We'll use the bytes of the string.
        val keyBytes = secretKey.toByteArray()
        val key =
            try {
                Keys.hmacShaKeyFor(keyBytes)
            } catch (e: Exception) {
                // Fallback or better handling could be added here
                throw RuntimeException("Invalid JWT secret key", e)
            }

        return Jwts
            .builder()
            .setSubject(user.id)
            .claim("email", user.email)
            .claim("name", user.name)
            .setExpiration(Date.from(Instant.now().plusSeconds(UtilConstants.ONE_DAY_IN_SECONDS.toLong())))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }
}
