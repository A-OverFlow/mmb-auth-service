package com.example.auth_service.auth.service

import com.example.auth_service.auth.Token
import com.example.auth_service.auth.jwt.JwtTokenProvider
import com.example.auth_service.auth.TokenRepository
import com.example.auth_service.auth.dto.GoogleUserDto
import com.example.auth_service.auth.dto.TokenResponse
import com.example.auth_service.auth.dto.TokenValidationResponse
import com.example.auth_service.common.exception.CustomException
import com.example.auth_service.common.exception.ErrorCode
import com.example.auth_service.member.dto.request.MemberCreateRequest
import com.example.auth_service.member.service.MemberService
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val jwtTokenProvider: JwtTokenProvider,
    private val tokenRepository: TokenRepository,
    private val memberService: MemberService
) {

    fun loginOrRegister(googleUser: GoogleUserDto): TokenResponse {
        val member = memberService.getMember(googleUser.email)
            ?: memberService.createMember(MemberCreateRequest.fromDto(googleUser))

        val accessToken = jwtTokenProvider.createAccessToken(member.email)
        val refreshToken = jwtTokenProvider.createRefreshToken(member.email)

        tokenRepository.save(Token(member.email, refreshToken))

        return TokenResponse(accessToken, refreshToken)
    }

    fun validateToken(token: String?): TokenValidationResponse {
        if (token == null || !token.startsWith("Bearer ")) {
            return TokenValidationResponse(valid = false, message = ErrorCode.INVALID_TOKEN.message)
        }

        val extractToken = token.substring(7)

        return if (jwtTokenProvider.validateToken(extractToken)) {
            val email = jwtTokenProvider.getEmail(extractToken)
            TokenValidationResponse(valid = true, email = email)
        } else {
            TokenValidationResponse(valid = false, message = ErrorCode.INVALID_ACCESS_TOKEN.message)
        }
    }

    fun reissueToken(refreshToken: String): TokenResponse {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw CustomException(ErrorCode.INVALID_REFRESH_TOKEN)
        }

        val email = jwtTokenProvider.getEmail(refreshToken)
        val saved = tokenRepository.findByEmail(email)
            ?: throw CustomException(ErrorCode.INVALID_REFRESH_TOKEN)

        if (saved.refreshToken != refreshToken) {
            throw CustomException(ErrorCode.INVALID_REFRESH_TOKEN)
        }

        val newAccessToken = jwtTokenProvider.createAccessToken(email)
        val newRefreshToken = jwtTokenProvider.createRefreshToken(email)

        saved.updateRefreshToken(newRefreshToken)
        tokenRepository.save(saved)

        return TokenResponse(newAccessToken, newRefreshToken)
    }

    fun logout(email: String) {
        tokenRepository.deleteById(email)
    }

    fun deleteAccount(id: Long) {
        val memberDto = (memberService.getMember(id)
            ?: throw CustomException(ErrorCode.MEMBER_NOT_FOUND))

        tokenRepository.deleteById(memberDto.email)
        memberService.deleteMember(memberDto.id)
    }
}