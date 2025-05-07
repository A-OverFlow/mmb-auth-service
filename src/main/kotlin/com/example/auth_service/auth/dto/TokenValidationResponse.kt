package com.example.auth_service.auth.dto

data class TokenValidationResponse(
    val valid: Boolean,
    val id: Long? = null,
    val message: String? = null
)