package com.example.auth_service.auth.service

import com.example.auth_service.auth.dto.CustomOAuth2User
import com.example.auth_service.common.exception.CustomException
import com.example.auth_service.common.exception.ErrorCode
import com.example.auth_service.member.service.MemberService
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val memberService: MemberService
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val delegate = DefaultOAuth2UserService()
        val oAuth2User = delegate.loadUser(userRequest)

        val email = oAuth2User.attributes["email"] as String

        val memberDto = (memberService.getMember(email)
            ?: throw CustomException(ErrorCode.MEMBER_NOT_FOUND))

        return CustomOAuth2User(memberDto)
    }
}