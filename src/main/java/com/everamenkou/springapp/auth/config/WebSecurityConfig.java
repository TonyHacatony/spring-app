package com.everamenkou.springapp.auth.config;

import com.everamenkou.springapp.auth.security.AuthenticationManager;
import com.everamenkou.springapp.auth.security.BearerTokenServerAuthenticationConverter;
import com.everamenkou.springapp.auth.security.JwtHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@EnableReactiveMethodSecurity
public class WebSecurityConfig {

    @Value("${jwt.secret}")
    private String secret;

    private final String[] publicRoutes = {"/api/v1/auth/register", "/api/v1/auth/login"};

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthenticationManager authenticationManager) {
        return http
                .csrf(customizeCSRF())
                .authorizeExchange(customizeAuthorizeExchange())
                .exceptionHandling(customizeExceptionsHandling())
                .addFilterAt(bearerAuthFilter(authenticationManager), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    private Customizer<ServerHttpSecurity.CsrfSpec> customizeCSRF() {
        return ServerHttpSecurity.CsrfSpec::disable;
    }

    private AuthenticationWebFilter bearerAuthFilter(AuthenticationManager authManager) {
        AuthenticationWebFilter bearerAuthWebFilter = new AuthenticationWebFilter(authManager);
        BearerTokenServerAuthenticationConverter bearerTokenServerAuthenticationConverter = new BearerTokenServerAuthenticationConverter(new JwtHandler(secret));
        bearerAuthWebFilter.setServerAuthenticationConverter(bearerTokenServerAuthenticationConverter);
        bearerAuthWebFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));
        return bearerAuthWebFilter;
    }

    private Customizer<ServerHttpSecurity.AuthorizeExchangeSpec> customizeAuthorizeExchange() {
        return authorizeExchangeSpec ->
                authorizeExchangeSpec
                        .pathMatchers(HttpMethod.OPTIONS)
                        .permitAll()
                        .pathMatchers(publicRoutes)
                        .permitAll()
                        .anyExchange()
                        .authenticated();
    }

    private Customizer<ServerHttpSecurity.ExceptionHandlingSpec> customizeExceptionsHandling() {
        return exceptionHandlingSpec ->
                exceptionHandlingSpec
                        .authenticationEntryPoint((save, e) -> {
                            log.error("In SecurityWebFilterChain - unauthorized error: {} ", e.getMessage());
                            return Mono.fromRunnable(() -> save.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
                        })
                        .accessDeniedHandler((save, e) -> {
                            log.error("In SecurityWebFilterChain - access denied: {} ", e.getMessage());
                            return Mono.fromRunnable(() -> save.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
                        });
    }
}
