package com.example.auth_service.auth.config

import com.example.auth_service.auth.handler.CustomOAuth2SuccessHandler
import com.example.auth_service.auth.handler.JwtAuthenticationEntryPoint
import com.example.auth_service.auth.jwt.JwtAuthenticationFilter
import com.example.auth_service.auth.service.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val customOAuth2SuccessHandler: CustomOAuth2SuccessHandler
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .exceptionHandling { it.authenticationEntryPoint(jwtAuthenticationEntryPoint) }
            .authorizeHttpRequests {
                it
                    .requestMatchers(
                        "/api/v1/auth/signup",
                        "/api/v1/auth/validate",
                        "/api/v1/auth/login"
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2Login {
                it.userInfoEndpoint {
                    it.userService(customOAuth2UserService)
                }
                it.successHandler(customOAuth2SuccessHandler)
            }

//        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}