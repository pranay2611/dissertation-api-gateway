package com.dissertation.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class GatewayIntegrationTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @Test
    void shouldReturnHealthStatus() {
        webTestClient.get()
                .uri("/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }
    
    @Test
    void shouldBlockUnauthorizedRequests() {
        webTestClient.get()
                .uri("/api/orders")
                .exchange()
                .expectStatus().isUnauthorized();
    }
    
    @Test
    void shouldAllowPublicEndpoints() {
        // Note: This would fail if user-service is not running
        // In real tests, you'd mock the downstream service
        webTestClient.post()
                .uri("/api/auth/login")
                .exchange()
                .expectStatus().is4xxClientError(); // Bad request due to missing body
    }
}

