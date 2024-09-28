package com.example.quizmaster.security;



import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider implements AuthenticationProvider {
    @Value("${jwt.token.key}")
    private String key;
    @Value("${jwt.token.ttl}")
    private Long ttl ;


    public String generateToken(String  phoneNumber){
        return Jwts.builder()
                .setSubject(phoneNumber)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ttl))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    public String getPhoneNumberFromToken(String token){
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
