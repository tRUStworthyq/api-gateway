package ru.sber.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.TokenRelayFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.web.servlet.function.RequestPredicates.path;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> dealServiceRoute(
            @Value("${DEAL_SERVICE_URL:http://deal-service:8080}") String dealServiceUrl) {
        return RouterFunctions.route()
                .route(path("/api/deals/**"), HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(URI.create(dealServiceUrl)))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build();
    }
}
