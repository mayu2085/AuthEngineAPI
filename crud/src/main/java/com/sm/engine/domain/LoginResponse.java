package com.sm.engine.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


/**
 * The login response.
 */
@Getter
@Setter
public class LoginResponse {
    /**
     * The JWT token.
     */
    @NotBlank
    private String token;

    /**
     * The user.
     */
    @NotNull
    @Valid
    private User user;
}
