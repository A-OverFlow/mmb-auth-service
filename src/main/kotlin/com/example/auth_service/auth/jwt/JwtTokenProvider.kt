package com.example.auth_service.auth.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") private val secretKey: String,
    @Value("\${jwt.access-token-expire-time}") private val accessTokenExpiration: Long,
    @Value("\${jwt.refresh-token-expire-time}") private val refreshTokenExpiration: Long,
) {
    fun createAccessToken(email: String): String = createToken(email, accessTokenExpiration)
    fun createRefreshToken(email: String): String = createToken(email, refreshTokenExpiration)

    private fun createToken(email: String, expired: Long): String {
        val claims = Jwts.claims().setSubject(email)
        val now = Date()
        val validity = Date(now.time + expired)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }

    fun getEmail(token: String): String =
        Jwts.parserBuilder().setSigningKey(secretKey).build()
            .parseClaimsJws(token)
            .body
            .subject

    fun validateToken(token: String): Boolean {
        return try {
            val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
            !claims.body.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }
}