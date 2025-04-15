package com.example.auth_service.member.dto.response

data class MemberGetResponse(
    val name: String,
    val email: String,
    val username: String
)