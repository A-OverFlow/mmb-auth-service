package com.example.auth_service.member.service

import com.example.auth_service.member.dto.MemberDto
import com.example.auth_service.member.dto.request.MemberCreateRequest
import com.example.auth_service.member.dto.response.MemberCreateResponse
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono

@Slf4j
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
                    uriBuilder.path("/api/v1/members")
                        .queryParam("email", email)
                        .build()
                }
                .retrieve()
                .onStatus({ it == HttpStatus.NOT_FOUND }) {
                    Mono.error(WebClientResponseException.create(
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        HttpHeaders.EMPTY,
                        ByteArray(0),
                        null
                    ))
                }
                .bodyToMono(MemberDto::class.java)
                .onErrorResume(WebClientResponseException.NotFound::class.java) {
                    Mono.empty() // 여기서 실제로 null 반환
                }
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

    fun createMember(request: MemberCreateRequest): MemberCreateResponse {
        return webClient.post()
            .uri("http://${host}:${port}/api/v1/members")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(MemberCreateResponse::class.java)
            .block()!!
    }

    fun deleteMember(id: Long): Mono<Void> {
        return webClient.delete()
            .uri("http://${host}:${port}/api/v1/members/{id}", id)
            .retrieve()
            .bodyToMono(Void::class.java)
    }
}