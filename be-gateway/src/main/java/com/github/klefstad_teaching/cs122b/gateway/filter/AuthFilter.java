package com.github.klefstad_teaching.cs122b.gateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.core.result.Result;
import com.github.klefstad_teaching.cs122b.core.result.ResultMap;
import com.github.klefstad_teaching.cs122b.core.security.JWTAuthenticationFilter;
import com.github.klefstad_teaching.cs122b.gateway.config.GatewayServiceConfig;
import com.github.klefstad_teaching.cs122b.gateway.model.AuthenticateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class AuthFilter implements GatewayFilter
{
    private static final Logger LOG = LoggerFactory.getLogger(AuthFilter.class);

    private final GatewayServiceConfig config;
    private final WebClient            webClient;

    @Autowired
    public AuthFilter(GatewayServiceConfig config)
    {
        this.config = config;
        this.webClient = WebClient.builder().build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
    {
        Optional<String> accessToken = getAccessTokenFromHeader(exchange);

        if (!accessToken.isPresent())
        {
            return setToFail(exchange);
        }

        return authenticate(accessToken.get())
                .flatMap(result -> result.code().equals(IDMResults.ACCESS_TOKEN_IS_VALID.code()) ?
                        chain.filter(exchange) : setToFail(exchange));
    }

    private Mono<Void> setToFail(ServerWebExchange exchange)
    {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    /**
     * Takes in a accessToken token and creates Mono chain that calls the idm and maps the value to
     * a Result
     *
     * @param accessToken a encodedJWT
     * @return a Mono that returns a Result
     */
    private Mono<Result> authenticate(String accessToken)
    {
        AuthenticateRequest requestBody = new AuthenticateRequest()
                .setAccessToken(accessToken);

        return webClient.post()
                .uri(config.getIdmAuthenticate())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(body -> ResultMap.fromCode(body.get("result").get("code").asInt()));
    }

    private Optional<String> getAccessTokenFromHeader(ServerWebExchange exchange)
    {
        String accessToken = exchange
                .getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (accessToken != null && accessToken.startsWith(JWTAuthenticationFilter.BEARER_PREFIX))
        {
            return Optional.of(accessToken.substring(JWTAuthenticationFilter.BEARER_PREFIX.length()));
        }
        else
        {
            return Optional.empty();
        }
    }
}
