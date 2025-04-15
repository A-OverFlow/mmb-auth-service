package com.example.auth_service.common.exception

data class ErrorResponse(
    val error: String,
    val message: String?,
)