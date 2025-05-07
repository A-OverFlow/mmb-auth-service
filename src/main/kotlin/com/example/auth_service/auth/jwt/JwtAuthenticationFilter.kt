package com.example.auth_service.auth.jwt

import com.example.auth_service.common.exception.CustomException
import com.example.auth_service.common.exception.ErrorCode
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    private val skipUrls = listOf(
        "/api/v1/auth/signup",
        "/api/v1/auth/login",
        "/api/v1/auth/google-id-token",
        "/oauth2/authorization/google",
        "/login/oauth2/code/google"
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestURI = request.requestURI

        if (skipUrls.any { requestURI.startsWith(it) }) {
            filterChain.doFilter(request, response)
            return
        }

        val token = resolveToken(request) ?: throw CustomException(ErrorCode.UNAUTHORIZED)

        if (jwtTokenProvider.validateToken(token)) {
            val id = jwtTokenProvider.getId(token)
            val userDetails = userDetailsService.loadUserByUsername("$id")
            val auth = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
            SecurityContextHolder.getContext().authentication = auth
        } else {
            throw CustomException(ErrorCode.UNAUTHORIZED)
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearer = request.getHeader("Authorization")

        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7)
        } else {
            return null
        }
    }
}