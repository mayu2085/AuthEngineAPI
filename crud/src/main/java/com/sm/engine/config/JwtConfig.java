package com.sm.engine.config;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

/**
 * The application config for jwt configurations.
 */
@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * The expiration time in milliseconds.
     */
    @Min(1)
    private long expirationTime;


    /**
     * The secret.
     */
    @NotBlank
    private String secret;

    /**
     * The auth header name
     */
    @NotBlank
    private String authHeader;

    /**
     * The token prefix.
     */
    @NotBlank
    private String tokenPrefix;
}