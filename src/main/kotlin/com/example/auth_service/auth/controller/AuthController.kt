package com.example.auth_service.auth.controller

import com.example.auth_service.auth.dto.GoogleLoginRequest
import com.example.auth_service.auth.dto.TokenRefreshRequest
import com.example.auth_service.auth.dto.TokenResponse
import com.example.auth_service.auth.dto.TokenValidationResponse
import com.example.auth_service.auth.jwt.JwtTokenProvider
import com.example.auth_service.auth.service.AuthService
import com.example.auth_service.auth.service.GoogleOAuthService
import com.example.auth_service.common.exception.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val googleOAuthService: GoogleOAuthService,
    private val authService: AuthService
) {

    @PostMapping("/signup")
    fun googleLogin(@RequestBody request: GoogleLoginRequest): ResponseEntity<TokenResponse> {
        val googleUser = googleOAuthService.verifyIdToken(request.idToken)
        val tokens = authService.loginOrRegister(googleUser)
        return ResponseEntity.ok(tokens)
    }

    @GetMapping("/validate")
    fun validateToken(@RequestHeader("Authorization") token: String?): ResponseEntity<TokenValidationResponse> {
        return ResponseEntity.ok(authService.validateToken(token))
    }

    @PostMapping("/reissue")
    fun reissue(@RequestBody request: TokenRefreshRequest): ResponseEntity<TokenResponse> {
        val tokens = authService.reissueToken(request.refreshToken)
        return ResponseEntity.ok(tokens)
    }
}

