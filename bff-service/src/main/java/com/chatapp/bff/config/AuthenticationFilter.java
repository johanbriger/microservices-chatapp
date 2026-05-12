package com.chatapp.bff.config;

import com.chatapp.bff.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public static class Config {
        // Här kan konfiguration läggas till om det behövs senare
    }

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 1. Kontrollera om Authorization-header finns
            if (!request.getHeaders().containsKey("Authorization")) {
                return onError(exchange, "Authorization header saknas", HttpStatus.UNAUTHORIZED);
            }

            // 2. Extrahera token från "Bearer <token>"
            String authHeader = request.getHeaders().getOrEmpty("Authorization").get(0);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
            }

            // 3. Validera token
            if (jwtUtil.isInvalid(authHeader)) {
                return onError(exchange, "Ogiltig eller utgången token", HttpStatus.UNAUTHORIZED);
            }

            // 4. Token OK - fortsätt till nästa tjänst
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
}