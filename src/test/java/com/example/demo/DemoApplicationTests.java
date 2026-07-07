package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.UUID;

@SpringBootTest
class DemoApplicationTests {

	@DynamicPropertySource
	static void googleOAuthProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.security.oauth2.client.registration.google.client-id", DemoApplicationTests::randomGoogleClientId);
		registry.add("spring.security.oauth2.client.registration.google.client-secret", () -> UUID.randomUUID().toString());
	}

	@Test
	void contextLoads() {
	}

	private static String randomGoogleClientId() {
		return UUID.randomUUID() + ".apps.googleusercontent.com";
	}
}
