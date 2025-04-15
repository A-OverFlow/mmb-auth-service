package com.example.auth_service.auth.dto

import com.example.auth_service.member.dto.MemberDto
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

class CustomOAuth2User(private val member: MemberDto) : OAuth2User {
    val email: String get() = member.email

    override fun getAttributes(): Map<String, Any> = mapOf("email" to member.email)
    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority(null))
    override fun getName(): String = member.email
}