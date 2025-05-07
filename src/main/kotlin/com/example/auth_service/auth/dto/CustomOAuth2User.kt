package com.example.auth_service.auth.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

class CustomOAuth2User(private val id: Long, private val name: String) : OAuth2User {
    override fun getAttributes(): Map<String, Any> = mapOf("id" to id)
    override fun getAuthorities(): Collection<GrantedAuthority> = listOf(SimpleGrantedAuthority("ROLE_MEMBER"))
    override fun getName(): String = name
}