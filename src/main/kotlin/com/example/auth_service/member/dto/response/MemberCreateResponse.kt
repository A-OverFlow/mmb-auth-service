package com.example.auth_service.member.dto.response

data class MemberCreateResponse(
    val id: Long,
    val name: String,
    val email: String,
    val username: String
)