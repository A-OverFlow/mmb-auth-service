package com.example.auth_service.auth.dto

data class TokenResponse(val accessToken: String, val refreshToken: String)