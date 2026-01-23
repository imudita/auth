package com.example.auth.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import com.example.auth.security.TokenBlacklist

@Component
class JwtTokenProvider(
    private val tokenBlacklist: TokenBlacklist
) {

    @Value("\${jwt.secret:mySecretKeyForJwtTokenGenerationAndValidation12345}")
    private lateinit var jwtSecret: String

    @Value("\${jwt.expiration:900}")
    private var jwtExpirationMs: Long = 0

    fun generateToken(
        username: String,
        userId: Long? = null,
        rights: List<String> = emptyList()
    ): String {
        val key = Keys.hmacShaKeyFor(jwtSecret.toByteArray())
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + jwtExpirationMs))
            .apply {
                if (userId != null) {
                    claim("userId", userId)
                }
                if (rights.isNotEmpty()) {
                    claim("rights", rights)
                }
            }
            .signWith(key)
            .compact()
    }

    fun getUsernameFromToken(token: String): String {
        val key = Keys.hmacShaKeyFor(jwtSecret.toByteArray())
        val claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
        return claims.payload.subject
    }

    fun validateToken(token: String): Boolean {
        return try {
            if (tokenBlacklist.isBlacklisted(token)) {
                return false
            }
            val key = Keys.hmacShaKeyFor(jwtSecret.toByteArray())
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getTokenExpiration(token: String): Long {
        val key = Keys.hmacShaKeyFor(jwtSecret.toByteArray())
        val claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
        return claims.payload.expiration.time
    }
}