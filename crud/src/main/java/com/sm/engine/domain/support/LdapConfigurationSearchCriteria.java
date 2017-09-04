package com.sm.engine.domain.support;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Presents the search criteria for searching Ldap configuration.
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class LdapConfigurationSearchCriteria extends NameSearchCriteria {
    /**
     * The enabled flag.
     */
    private Boolean enabled;
}
