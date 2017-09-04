package com.sm.engine.config;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.query.SearchScope;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * The test ldap config.
 */
@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "ldap")
public class TestLdapConfig {

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