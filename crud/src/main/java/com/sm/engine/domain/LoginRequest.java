package com.sm.engine.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;


/**
 * The login request.
 */
@Getter
@Setter
public class LoginRequest {
    /**
     * The username.
     */
    @NotBlank
    private String username;

    /**
     * The password.
     */
    @NotBlank
    private String password;
}
