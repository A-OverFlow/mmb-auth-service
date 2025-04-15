package com.example.auth_service.member.dto.request

import com.example.auth_service.auth.dto.GoogleUserDto

class MemberCreateRequest(
    val name: String,
    val email: String,
    val username: String
) {
    companion object {

        fun fromDto(googleUser: GoogleUserDto): MemberCreateRequest {
            return MemberCreateRequest(
                name = googleUser.name,
                email = googleUser.email,
                username = googleUser.name
            )
        }
    }
}