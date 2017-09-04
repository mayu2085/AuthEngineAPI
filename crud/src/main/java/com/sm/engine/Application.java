package com.sm.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.context.request.RequestAttributes;

import java.util.Map;

/**
 * The application configuration.
 */
@SpringBootApplication
@EnableMongoAuditing
public class Application {

    /**
     * The error attributes.
     * Handle exceptions thrown from Filter.
     *
     * @return the custom error attributes.
     */
    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            /**
             * Format error attributes with code and message key only.
             * @param requestAttributes the request attributes.
             * @param includeStackTrace the include stack trace flag.
             * @return the error attributes with code and message key only.
             */
            @Override
            public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean
                    includeStackTrace) {
                Map<String, Object> errorAttributes = super.getErrorAttributes(requestAttributes, includeStackTrace);
                if (!errorAttributes.containsKey("message")
                        || errorAttributes.size() != 1) {
                    Throwable error = getError(requestAttributes);
                    Object status = errorAttributes.getOrDefault("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
                    if (error instanceof UsernameNotFoundException || error instanceof ExpiredJwtException) {
                        status = HttpStatus.UNAUTHORIZED.value();
                    }
                    if (error instanceof DisabledException) {
                        status = HttpStatus.FORBIDDEN.value();
                    }
                    Object message = errorAttributes.getOrDefault("message",
                            error != null && error.getMessage() != null ? error.getMessage()
                                    : "Unexpected error");
                    errorAttributes.clear();
                    errorAttributes.put("message", message);
                    requestAttributes.setAttribute("javax.servlet.error.status_code", status, 0);
                }
                return errorAttributes;
            }
        };
    }


    /**
     * Configure the object mapper.
     *
     * @return the object mapper
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    /**
     * Main entry point of the application.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
