package com.example.auth_service.auth.service

import com.example.auth_service.auth.dto.GoogleUserDto
import com.example.auth_service.common.exception.CustomException
import com.example.auth_service.common.exception.ErrorCode
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GoogleOAuthService(
    @Value("\${spring.security.oauth2.client.registration.google.client-id}")
    private val clientId: String
) {
    private val transport = GoogleNetHttpTransport.newTrustedTransport()
    private val jsonFactory = JacksonFactory.getDefaultInstance()

    private val verifier: GoogleIdTokenVerifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
        .setAudience(listOf(clientId))
        .build()

    fun verifyIdToken(idTokenString: String): GoogleUserDto {
        try {
            val idToken: GoogleIdToken = verifier.verify(idTokenString)
            val payload = idToken.payload

            return GoogleUserDto(
                provider = "GOOGLE",
                providerId = payload["sub"] as String,
                email = payload["email"] as String,
                name = payload["name"] as String
            )
        } catch (e: Exception) {
            throw CustomException(ErrorCode.INVALID_GOOGLE_ID_TOKEN)
        }
    }
}