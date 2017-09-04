package com.sm.engine.controller;

import com.sm.engine.domain.LdapConfiguration;
import com.sm.engine.domain.support.LdapConfigurationSearchCriteria;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The controller of ldap-configurations end-points.
 */
@RestController
@RequestMapping("/ldap-configurations")
public class LdapConfigurationController
        extends BaseController<LdapConfiguration, LdapConfigurationSearchCriteria> {
}
