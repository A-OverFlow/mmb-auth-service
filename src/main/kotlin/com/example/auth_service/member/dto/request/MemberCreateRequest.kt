package com.example.auth_service.member.dto.request

import com.example.auth_service.auth.dto.GoogleUserDto

class MemberCreateRequest(
    val provider: String,
    val providerId: String,
    val name: String,
    val email: String,
    val picture: String
) {
    constructor(oAuth2UserAttributes: Map<String, Any>) : this(
        provider = "GOOGLE",
        providerId = oAuth2UserAttributes["sub"] as String,
        name = oAuth2UserAttributes["name"] as String,
        email = oAuth2UserAttributes["email"] as String,
        picture = oAuth2UserAttributes["picture"] as String
    )

    companion object {

        fun fromDto(googleUser: GoogleUserDto): MemberCreateRequest {
            return MemberCreateRequest(
                provider = googleUser.provider,
                providerId = googleUser.providerId,
                name = googleUser.name,
                email = googleUser.email,
                picture = googleUser.picture
            )
        }
    }
}
