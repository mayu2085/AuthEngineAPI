package com.sm.engine.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;


/**
 * The ldap user attributes request.
 */
@Getter
@Setter
public class UserAttributesRequest {
    /**
     * The user dn.
     */
    @NotBlank
    private String userDN;

    /**
     * The user attributes to lookup.
     */
    private String[] attributes;
}
