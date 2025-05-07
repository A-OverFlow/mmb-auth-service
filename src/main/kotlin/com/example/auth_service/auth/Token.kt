package com.example.auth_service.auth

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("refresh_tokens")
class Token(

    @Id
    val id: Long,

    var refreshToken: String
) {

    fun updateRefreshToken(refreshToken: String) {
        this.refreshToken = refreshToken
    }
}