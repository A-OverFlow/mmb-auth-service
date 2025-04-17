package com.example.auth_service.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    @Value("\${common.service.member.host}")
    private lateinit var host: String

    @Value("\${common.service.member.port}")
    private var port: Int = 0

    @Bean
    fun webClient(): WebClient {
        val baseUrl = "http://$host:$port"
        return WebClient.builder()
            .baseUrl(baseUrl)
            .build()
    }
}