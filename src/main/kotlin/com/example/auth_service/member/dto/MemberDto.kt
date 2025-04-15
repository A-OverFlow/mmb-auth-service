package com.example.auth_service.member.dto

data class MemberDto (
    val id: Long,
    val name: String,
    val email: String,
    val username: String
)