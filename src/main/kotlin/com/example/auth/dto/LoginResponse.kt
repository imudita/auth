package com.example.auth.dto

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val username: String? = null
)