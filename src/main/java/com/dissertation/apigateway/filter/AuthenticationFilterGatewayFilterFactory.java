package com.dissertation.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFilterGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthenticationFilterGatewayFilterFactory.Config> {

    private final AuthenticationFilter authenticationFilter;

    public AuthenticationFilterGatewayFilterFactory(AuthenticationFilter authenticationFilter) {
        super(Config.class);
        this.authenticationFilter = authenticationFilter;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return authenticationFilter;
    }

    public static class Config {
        // Configuration properties can be added here if needed
    }
}

