package com.sm.engine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

/**
 * The LDAP configuration document definition.
 */
@Document
@CompoundIndex(name = "name", unique = true, def = "{'name' : 1 }")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class LdapConfiguration extends NamedDocument {

    /**
     * The url.
     */
    @NotNull
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
     * The enabled flag.
     */
    @NotNull
    private Boolean enabled;
}
