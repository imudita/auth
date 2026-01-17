package com.example.auth.service

import com.example.auth.entity.User
import com.example.auth.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository
) {

    private val passwordEncoder = BCryptPasswordEncoder()

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
}
