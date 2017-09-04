package com.sm.engine.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * Presents the LDAP attribute name and value pairs associated to Rule.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name"})
public class LdapAttributeNameValue {

    /**
     * The LDAP attribute name.
     */
    @NotNull
    private String name;

    /**
     * The value.
     */
    @NotNull
    private String value;
}
