package com.example.auth.service

import com.example.auth.entity.User
import com.example.auth.repository.UserRepository
import com.example.auth.security.JwtTokenProvider
import com.example.auth.security.TokenBlacklist
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

@ExtendWith(MockitoExtension::class)
class AuthServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Mock
    private lateinit var tokenBlacklist: TokenBlacklist

    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var authService: AuthService

    @BeforeEach
    fun setup() {
        passwordEncoder = BCryptPasswordEncoder()
        authService = AuthService(
            userRepository,
            jwtTokenProvider,
            passwordEncoder,
            tokenBlacklist
        )
    }

    @Test
    fun `login with valid credentials should return true`() {
        // Arrange
        val username = "testuser"
        val password = "password123"
        val encodedPassword = passwordEncoder.encode(password)
        val user = User(username = username, password = encodedPassword)

        org.mockito.Mockito.`when`(userRepository.findByUsername(username))
            .thenReturn(user)

        // Act
        val result = authService.login(username, password)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `login with invalid password should return false`() {
        // Arrange
        val username = "testuser"
        val password = "wrongpassword"
        val encodedPassword = passwordEncoder.encode("correctpassword")
        val user = User(username = username, password = encodedPassword)

        org.mockito.Mockito.`when`(userRepository.findByUsername(username))
            .thenReturn(user)

        // Act
        val result = authService.login(username, password)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `login with non-existent user should return false`() {
        // Arrange
        val username = "nonexistent"
        org.mockito.Mockito.`when`(userRepository.findByUsername(username))
            .thenReturn(null)

        // Act
        val result = authService.login(username, "password")

        // Assert
        assertFalse(result)
    }

    @Test
    fun `changePassword with correct old password should return true`() {
        // Arrange
        val username = "testuser"
        val oldPassword = "oldpass123"
        val newPassword = "newpass123"
        val encodedOldPassword = passwordEncoder.encode(oldPassword)
        val user = User(username = username, password = encodedOldPassword)

        org.mockito.Mockito.`when`(userRepository.findByUsername(username))
            .thenReturn(user)

        // Act
        val result = authService.changePassword(username, oldPassword, newPassword)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `changePassword with incorrect old password should return false`() {
        // Arrange
        val username = "testuser"
        val correctOldPassword = "correctoldpass"
        val wrongOldPassword = "wrongoldpass"
        val newPassword = "newpass123"
        val encodedPassword = passwordEncoder.encode(correctOldPassword)
        val user = User(username = username, password = encodedPassword)

        org.mockito.Mockito.`when`(userRepository.findByUsername(username))
            .thenReturn(user)

        // Act
        val result = authService.changePassword(username, wrongOldPassword, newPassword)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `logout should add token to blacklist`() {
        // Arrange
        val token = "test_token_123"
        val expirationTime = System.currentTimeMillis() + 86400000

        org.mockito.Mockito.`when`(jwtTokenProvider.getTokenExpiration(token))
            .thenReturn(expirationTime)

        // Act
        val result = authService.logout(token)

        // Assert
        assertTrue(result)
        org.mockito.Mockito.verify(tokenBlacklist).add(token, expirationTime)
    }
}