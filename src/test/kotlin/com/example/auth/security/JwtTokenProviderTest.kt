package com.example.auth.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.util.ReflectionTestUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

@ExtendWith(MockitoExtension::class)
class JwtTokenProviderTest {

    @Mock
    private lateinit var tokenBlacklist: TokenBlacklist

    private lateinit var jwtTokenProvider: JwtTokenProvider

    @BeforeEach
    fun setup() {
        jwtTokenProvider = JwtTokenProvider(tokenBlacklist)
        // Set secret via reflection for testing
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "mySecretKeyForJwtTokenGenerationAndValidation12345")
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", 86400000L)
    }

    @Test
    fun `generateToken should create valid JWT`() {
        // Arrange
        val username = "testuser"

        // Act
        val token = jwtTokenProvider.generateToken(username)

        // Assert
        assertTrue(token.isNotEmpty())
        assertTrue(token.contains(".")) // JWT format: header.payload.signature
    }

    @Test
    fun `validateToken with valid token should return true`() {
        // Arrange
        val username = "testuser"
        val token = jwtTokenProvider.generateToken(username)

        // Act
        val result = jwtTokenProvider.validateToken(token)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `validateToken with invalid token should return false`() {
        // Arrange
        val invalidToken = "invalid.token.here"

        // Act
        val result = jwtTokenProvider.validateToken(invalidToken)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `getUsernameFromToken should extract correct username`() {
        // Arrange
        val username = "testuser"
        val token = jwtTokenProvider.generateToken(username)

        // Act
        val extractedUsername = jwtTokenProvider.getUsernameFromToken(token)

        // Assert
        assertTrue(extractedUsername == username)
    }

    @Test
    fun `generateToken should include optional claims when provided`() {
        // Arrange
        val username = "testuser"
        val userId = 123L
        val rights = listOf("READ", "WRITE")

        // Act
        val token = jwtTokenProvider.generateToken(username, userId, rights)

        // Assert
        val key = Keys.hmacShaKeyFor("mySecretKeyForJwtTokenGenerationAndValidation12345".toByteArray())
        val claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload

        assertEquals(username, claims.subject)
        assertEquals(userId, (claims["userId"] as Number).toLong())
        assertEquals(rights, claims["rights"] as List<*>)
    }
}