package com.sm.engine.security;

import com.sm.engine.config.JwtConfig;
import com.sm.engine.domain.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * The service provides operations to encode/decode jwt token.
 */
@Service
public class JwtService {
    /**
     * Inject jwt config.
     */
    @Autowired
    private JwtConfig jwtConfig;

    /**
     * Decode jwt token and get username.
     *
     * @param token the token
     * @return jwt token from user.
     */
    public String decode(String token) {
        return Jwts.parser()
                .setSigningKey(jwtConfig.getSecret())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Generate jwt token from user.
     *
     * @param user the user.
     * @return jwt token from user.
     */
    public String encode(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpirationTime()))
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret())
                .compact();
    }
}
