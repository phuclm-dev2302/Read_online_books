package com.example.read_book_online.middleware;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties("app.security.jwt")
public class JwtProperties {
    private String privateKey;
    private String prefix;
    private String authHeader;
    private long keyExpiresAt;
}