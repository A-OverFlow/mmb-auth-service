package com.example.auth_service.auth.service

import com.example.auth_service.common.exception.CustomException
import com.example.auth_service.common.exception.ErrorCode
import com.example.auth_service.member.service.MemberService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val memberService: MemberService
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {

        val memberDto = (memberService.getMember(email)
            ?: throw CustomException(ErrorCode.MEMBER_NOT_FOUND))

        return User(memberDto.username, memberDto.email, emptyList())
    }
}