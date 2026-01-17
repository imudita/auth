package com.example.auth.controller

import com.example.auth.dto.*
import com.example.auth.service.AuthService
import jakarta.annotation.PostConstruct
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostConstruct
    fun init() {
        // dummy user
        authService.createUser("admin", "admin123")
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ApiResponse {
        val success = authService.login(request.username, request.password)
        return if (success) {
            ApiResponse(true, "Login success")
        } else {
            ApiResponse(false, "Invalid username or password")
        }
    }

    @PostMapping("/change-password")
    fun changePassword(
        @Valid @RequestBody request: ChangePasswordRequest
    ): ApiResponse {
        val success = authService.changePassword(
            request.username,
            request.oldPassword,
            request.newPassword
        )
        return if (success) {
            ApiResponse(true, "Password changed successfully")
        } else {
            ApiResponse(false, "Old password is incorrect")
        }
    }
}
