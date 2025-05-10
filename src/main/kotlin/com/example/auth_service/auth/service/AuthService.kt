package com.example.auth_service.auth.service

import com.example.auth_service.auth.Token
import com.example.auth_service.auth.TokenRepository
import com.example.auth_service.auth.dto.GoogleUserDto
import com.example.auth_service.auth.dto.TokenResponse
import com.example.auth_service.auth.dto.TokenValidationResponse
import com.example.auth_service.auth.jwt.JwtTokenProvider
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
        val member = memberService.createMember(MemberCreateRequest.fromDto(googleUser))

        val accessToken = jwtTokenProvider.createAccessToken(member.id)
        val refreshToken = jwtTokenProvider.createRefreshToken(member.id)

        tokenRepository.save(Token(member.id, refreshToken))

        return TokenResponse(accessToken, refreshToken)
    }

    fun validateToken(token: String?): TokenValidationResponse {
        if (token == null || !token.startsWith("Bearer ")) {
            return TokenValidationResponse(valid = false, message = ErrorCode.INVALID_TOKEN.message)
        }

        val extractToken = token.substring(7)

        return if (jwtTokenProvider.validateToken(extractToken)) {
            val id = jwtTokenProvider.getId(extractToken)
            TokenValidationResponse(valid = true, id = id)
        } else {
            TokenValidationResponse(valid = false, message = ErrorCode.INVALID_ACCESS_TOKEN.message)
        }
    }

    fun reissueToken(refreshToken: String): TokenResponse {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw CustomException(ErrorCode.INVALID_REFRESH_TOKEN)
        }

        val id = jwtTokenProvider.getId(refreshToken)
        val saved = tokenRepository.findById(id)
            .orElseThrow { throw CustomException(ErrorCode.INVALID_REFRESH_TOKEN) }

        if (saved.refreshToken != refreshToken) {
            throw CustomException(ErrorCode.INVALID_REFRESH_TOKEN)
        }

        val newAccessToken = jwtTokenProvider.createAccessToken(id)
        val newRefreshToken = jwtTokenProvider.createRefreshToken(id)

        saved.updateRefreshToken(newRefreshToken)
        tokenRepository.save(saved)

        return TokenResponse(newAccessToken, newRefreshToken)
    }

    fun logout(id: Long) {
        tokenRepository.deleteById(id)
    }

    fun deleteAccount(id: Long) {
        val memberDto = (memberService.getMember(id)
            ?: throw CustomException(ErrorCode.MEMBER_NOT_FOUND))

        tokenRepository.deleteById(memberDto.id)
        memberService.deleteMember(memberDto.id)
    }
}