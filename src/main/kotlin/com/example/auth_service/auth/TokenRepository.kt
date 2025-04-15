package com.example.auth_service.auth

import org.springframework.data.repository.CrudRepository

interface TokenRepository : CrudRepository<Token, String> {
    fun findByEmail(email: String): Token?
}