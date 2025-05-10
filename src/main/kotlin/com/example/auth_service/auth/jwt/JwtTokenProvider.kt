package com.example.auth_service.auth.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.Date
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") private val secretKey: String,
    @Value("\${jwt.access-token-expire-time}") private val accessTokenExpiration: Long,
    @Value("\${jwt.refresh-token-expire-time}") private val refreshTokenExpiration: Long,
) {
    fun createAccessToken(id: Long): String = createToken(id, accessTokenExpiration)
    fun createRefreshToken(id: Long): String = createToken(id, refreshTokenExpiration)

    private fun createToken(id: Long, expired: Long): String {
        val claims = Jwts.claims().setSubject("$id")
        val now = Date()
        val validity = Date(now.time + expired)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }

    fun getId(token: String): Long =
        Jwts.parserBuilder().setSigningKey(secretKey).build()
            .parseClaimsJws(token)
            .body
            .subject.toLong()

    fun validateToken(token: String): Boolean {
        return try {
            val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
            !claims.body.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }
}