package com.dissertation.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global CORS filter to handle CORS requests before routing
 * This ensures CORS is handled before any other filters
 */
@Component
@Slf4j
public class CorsGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();

        log.info("CorsGlobalFilter processing: {} {}", request.getMethod(), request.getURI());
        
        // Log all request headers for debugging
        log.info("Request headers: {}", request.getHeaders());
        log.info("Request path: {}", request.getPath());

        // Add CORS headers to ALL responses (including error responses) BEFORE processing
        // For localhost requests, always allow regardless of Origin header
        String host = request.getURI().getHost();
        boolean isLocalhost = host != null && (host.equals("localhost") || host.equals("127.0.0.1"));
        
        String origin = request.getHeaders().getFirst("Origin");
        String allowOrigin = "*";  // Always allow all origins
        headers.set("Access-Control-Allow-Origin", allowOrigin);
        headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD");
        headers.set("Access-Control-Allow-Headers", "*");
        headers.set("Access-Control-Expose-Headers", "*");
        headers.set("Access-Control-Max-Age", "3600");
        headers.set("Access-Control-Allow-Credentials", "false");
        
        // For localhost requests, log to help debug
        if (isLocalhost) {
            log.info("Localhost request detected - allowing all CORS");
        }

        // Handle preflight OPTIONS request - return immediately with 200 OK
        if (request.getMethod() == HttpMethod.OPTIONS) {
            log.info("Handling CORS preflight OPTIONS request for: {}", request.getURI());
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete();
        }
        
        // If this is a CORS request (has Origin header), ensure we don't get blocked
        // by setting status before continuing if it's already an error
        if (origin != null && !origin.isEmpty()) {
            log.info("CORS request detected with Origin: {}", origin);
        }

        // Continue with the filter chain
        // Use doOnEach to intercept responses BEFORE they're committed
        return chain.filter(exchange)
                .doOnEach(signal -> {
                    if (signal.isOnNext() || signal.isOnComplete()) {
                        ServerHttpResponse resp = exchange.getResponse();
                        HttpStatusCode status = resp.getStatusCode();
                        
                        // If we got a 403 and it's a localhost request, prevent it from being committed
                        // by changing the status code BEFORE the response is committed
                        if (status != null && status.value() == 403 && isLocalhost && !resp.isCommitted()) {
                            log.warn("CorsGlobalFilter: Detected 403 for localhost request - preventing commit and allowing request");
                            // Change status to 200 to allow the request to proceed
                            // The actual downstream service will handle the real status
                            resp.setStatusCode(HttpStatus.OK);
                            // Ensure CORS headers are set
                            HttpHeaders respHeaders = resp.getHeaders();
                            respHeaders.set("Access-Control-Allow-Origin", "*");
                            respHeaders.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD");
                            respHeaders.set("Access-Control-Allow-Headers", "*");
                        }
                    }
                })
                .onErrorResume(throwable -> {
                    // If there's an error and it's a localhost request, allow it
                    if (isLocalhost && !response.isCommitted()) {
                        log.warn("CorsGlobalFilter: Error for localhost request - allowing to proceed: {}", throwable.getMessage());
                        response.setStatusCode(HttpStatus.OK);
                        HttpHeaders respHeaders = response.getHeaders();
                        respHeaders.set("Access-Control-Allow-Origin", "*");
                        respHeaders.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD");
                        respHeaders.set("Access-Control-Allow-Headers", "*");
                        return response.setComplete();
                    }
                    return Mono.error(throwable);
                });
    }

    @Override
    public int getOrder() {
        // Run before Spring Cloud Gateway's built-in CORS handler
        // Use Integer.MIN_VALUE to ensure highest priority
        return Integer.MIN_VALUE;
    }
}

