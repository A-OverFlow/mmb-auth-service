package com.example.auth_service.auth.dto

data class TokenValidationResponse(
    val valid: Boolean,
    val email: String? = null,
    val message: String? = null
)