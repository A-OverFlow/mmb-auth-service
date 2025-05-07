package com.example.auth_service.auth.service

import com.example.auth_service.auth.dto.CustomOAuth2User
import com.example.auth_service.member.dto.request.MemberCreateRequest
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

        val member = memberService.createMember(MemberCreateRequest(oAuth2User.attributes))
        return CustomOAuth2User(member.id, oAuth2User.attributes["name"] as String)
    }
}
