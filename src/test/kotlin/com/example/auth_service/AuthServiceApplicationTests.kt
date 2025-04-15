package com.example.auth_service

import com.example.auth_service.config.EmbeddedRedisConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Import(EmbeddedRedisConfig::class)
@SpringBootTest
class AuthServiceApplicationTests {

	@Test
	fun contextLoads() {
	}

}
