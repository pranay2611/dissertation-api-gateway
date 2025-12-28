package com.dissertation.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;

/**
 * CORS configuration for API Gateway
 * Ensures all endpoints accept cross-origin requests
 * Handles both preflight OPTIONS requests and actual requests
 */
@Configuration
public class CorsConfig {

    // Disabled - using CorsGlobalFilter instead to avoid conflicts
    // @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOriginPatterns(Arrays.asList("*"));
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"));
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        corsConfig.setExposedHeaders(Arrays.asList("*"));
        corsConfig.setAllowCredentials(false);
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

    /**
     * Additional filter to handle OPTIONS requests that might not be caught by CORS
     * Disabled - using CorsGlobalFilter instead
     */
    // @Bean
    public WebFilter corsOptionsFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            if (exchange.getRequest().getMethod().name().equals("OPTIONS")) {
                exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponse().getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
                exchange.getResponse().getHeaders().add("Access-Control-Allow-Headers", "*");
                exchange.getResponse().getHeaders().add("Access-Control-Max-Age", "3600");
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.OK);
                return exchange.getResponse().setComplete();
            }
            return chain.filter(exchange);
        };
    }
}

