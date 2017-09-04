package com.sm.engine.controller;

import com.sm.engine.service.LdapAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Defines the ldap-attributes searching end-point. There is no add/update/delete end-points for
 * ldap-attributes.
 */
@RestController
@RequestMapping("/ldap-attributes")
public class LdapAttributeController {

    /**
     * The service instance.
     */
    @Autowired
    private LdapAttributeService ldapAttributeService;

    /**
     * Gets the names of all LDAP attributes which are enabled.
     *
     * @return names of all LDAP attributes which are enabled
     */
    @GetMapping
    public List<String> getAllNames() {
        return ldapAttributeService.getAllNames();
    }
}
