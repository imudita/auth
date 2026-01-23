package com.example.auth.service

import com.example.auth.entity.User
import com.example.auth.repository.UserRepository
import com.example.auth.security.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import com.example.auth.security.TokenBlacklist

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
    private val tokenBlacklist: TokenBlacklist
) {

    fun login(username: String, password: String): Boolean {
        val user = userRepository.findByUsername(username)
            ?: return false

        return passwordEncoder.matches(password, user.password)
    }

    fun changePassword(username: String, oldPassword: String, newPassword: String): Boolean {
        val user = userRepository.findByUsername(username)
            ?: return false

        if (!passwordEncoder.matches(oldPassword, user.password)) {
            return false
        }

        user.password = passwordEncoder.encode(newPassword)
        userRepository.save(user)
        return true
    }

    // helper untuk seed data
    fun createUser(username: String, password: String) {
        val user = User(
            username = username,
            password = passwordEncoder.encode(password)
        )
        userRepository.save(user)
    }

    fun generateToken(username: String): String {
        return jwtTokenProvider.generateToken(username)
    }

    fun logout(token: String): Boolean {
        return try {
            val expirationTime = jwtTokenProvider.getTokenExpiration(token)
            tokenBlacklist.add(token, expirationTime)
            true
        } catch (e: Exception) {
            false
        }
    }
}
