package com.example.auth.controller

import com.example.auth.dto.LoginResponse
import com.example.auth.dto.*
import com.example.auth.service.AuthService
import jakarta.annotation.PostConstruct
import jakarta.validation.Valid
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import jakarta.servlet.http.HttpServletRequest

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
    fun login(@Valid @RequestBody request: LoginRequest): LoginResponse {
        val success = authService.login(request.username, request.password)
        return if (success) {
            val token = authService.generateToken(request.username)
            LoginResponse(true, "Login success", token, request.username)
        } else {
            LoginResponse(false, "Invalid username or password")
        }
    }

    @PostMapping("/change-password")
    fun changePassword(
        @Valid @RequestBody request: ChangePasswordRequest
    ): ApiResponse {
        val username = SecurityContextHolder.getContext().authentication?.name
            ?: return ApiResponse(false, "Unauthorized")
        val success = authService.changePassword(
            username,
            request.oldPassword,
            request.newPassword
        )
        return if (success) {
            ApiResponse(true, "Password changed successfully")
        } else {
            ApiResponse(false, "Old password is incorrect")
        }
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ApiResponse {
        val token = getTokenFromRequest(request)
        return if (token != null && authService.logout(token)) {
            ApiResponse(true, "Logout successful")
        } else {
            ApiResponse(false, "Logout failed")
        }
    }

    private fun getTokenFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }
}
