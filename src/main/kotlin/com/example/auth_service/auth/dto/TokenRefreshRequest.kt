package com.example.auth_service.auth.dto

data class TokenRefreshRequest(
    val refreshToken: String
)