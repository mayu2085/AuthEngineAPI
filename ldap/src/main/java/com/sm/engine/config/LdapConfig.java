package com.sm.engine.config;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.ldap.query.SearchScope;

import javax.validation.constraints.NotNull;

/**
 * The ldap config.
 */
@Getter
@Setter
public class LdapConfig {

    /**
     * The ldap server url.
     */
    @NotBlank
    private String url;

    /**
     * The root.
     */
    @NotNull
    private String root;

    /**
     * The user dn.
     */
    @NotNull
    private String userDn;

    /**
     * The user password.
     */
    @NotNull
    private String password;

    /**
     * The user search base.
     */
    @NotBlank
    private String userSearchBase;

    /**
     * The user attribute.
     */
    @NotBlank
    private String userAttribute;

    /**
     * The ldap server search scope.
     */
    private SearchScope searchScope;
}