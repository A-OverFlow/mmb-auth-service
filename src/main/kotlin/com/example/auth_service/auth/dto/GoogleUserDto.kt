package com.example.auth_service.auth.dto

data class GoogleUserDto(
    val provider: String,
    val providerId: String,
    val email: String,
    val name: String,
    val picture: String
)