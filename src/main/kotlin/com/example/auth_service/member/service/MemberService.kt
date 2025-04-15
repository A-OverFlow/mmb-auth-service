package com.example.auth_service.member.service

import com.example.auth_service.member.dto.MemberDto
import com.example.auth_service.member.dto.request.MemberCreateRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono

@Service
class MemberService(
    private val webClient: WebClient,
    @Value("\${common.service.member.host}") private val host: String,
    @Value("\${common.service.member.port}") private val port: Int,
) {

    fun getMember(email: String): MemberDto? {
        return try {
            webClient.get()
                .uri { uriBuilder ->
                    uriBuilder.path("http://${host}:${port}/api/v1/members/check")
                        .queryParam("email", email)
                        .build()
                }
                .retrieve()
                .onStatus({ it == HttpStatus.NOT_FOUND }) {
                    Mono.empty()
                }
                .bodyToMono(MemberDto::class.java)
                .block()
        } catch (e: WebClientResponseException.NotFound) {
            null
        }
    }

    fun getMember(id: Long): MemberDto? {
        return webClient.get()
            .uri("http://${host}:${port}/api/v1/members/{id}", id)
            .retrieve()
            .bodyToMono(MemberDto::class.java)
            .block()
    }

    fun createMember(request: MemberCreateRequest): MemberDto {
        return webClient.post()
            .uri("http://${host}:${port}/api/v1/members")
            .bodyValue(request)
            .retrieve()
            .onStatus({ it == HttpStatus.NOT_FOUND }) {
                Mono.empty()
            }
            .bodyToMono(MemberDto::class.java)
            .block()!!
    }

    fun deleteMember(id: Long): Mono<Void> {
        return webClient.delete()
            .uri("http://${host}:${port}/api/v1/members/{id}", id)
            .retrieve()
            .bodyToMono(Void::class.java)
    }
}