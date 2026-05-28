package ru.sber.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.TokenRelayFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.web.servlet.function.RequestPredicates.path;

@Configuration
public class GatewayConfig {

    @Bean("gatewayProxyRequestFactory")
    public ClientHttpRequestFactory gatewayProxyRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5_000);
        factory.setReadTimeout(30_000);
        return factory;
    }

    @Bean
    public RouterFunction<ServerResponse> rootRedirect() {
        return RouterFunctions.route()
                .GET("/", request -> ServerResponse
                        .temporaryRedirect(URI.create("/ui"))
                        .build())
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> dealServiceRoute(
            @Value("${DEAL_SERVICE_URL:http://deal-service:8080}") String dealServiceUrl) {
        return RouterFunctions.route()
                .route(path("/api/deals/**"), HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(URI.create(dealServiceUrl)))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> transactionServiceRoute(
            @Value("${TRANSACTION_SERVICE_URL:http://transaction-service:8080}") String transactionServiceUrl) {
        return RouterFunctions.route()
                .route(path("/api/transactions/**"), HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(URI.create(transactionServiceUrl)))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> frontendRoute(
            @Value("${FRONTEND_URL:http://frontend:3000}") String frontendUrl) {
        return RouterFunctions.route()
                .route(path("/ui/**"), HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(URI.create(frontendUrl)))
                .build();
    }
}
