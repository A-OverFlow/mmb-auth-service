package com.example.auth_service.auth.handler

import com.example.auth_service.auth.dto.CustomOAuth2User
import com.example.auth_service.auth.dto.TokenResponse
import com.example.auth_service.auth.jwt.JwtTokenProvider
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class CustomOAuth2SuccessHandler(
    private val jwtTokenProvider: JwtTokenProvider,
    private val objectMapper: ObjectMapper
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val principal = authentication.principal as CustomOAuth2User
        val accessToken = jwtTokenProvider.createAccessToken(principal.email)
        val refreshToken = jwtTokenProvider.createRefreshToken(principal.email)
        val responseBody = TokenResponse(accessToken, refreshToken)

        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.status = HttpServletResponse.SC_OK
        response.writer.write(objectMapper.writeValueAsString(responseBody))
    }
}