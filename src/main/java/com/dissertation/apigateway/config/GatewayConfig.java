package com.dissertation.apigateway.config;

import com.dissertation.apigateway.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    
    @Autowired
    private AuthenticationFilter authenticationFilter;
    
    // Routes are configured in application.yml
    // This class can be used for additional programmatic route configuration if needed
}

