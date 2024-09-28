package com.example.quizmaster.security;

public class SecurityConstants {
    public static final String[] WHITE_LIST = {
            "/api/auth/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/swagger-config",
            "/swagger-resources/**",
            "/webjars/**",
            "/ws/**",
    };
}
