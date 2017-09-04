package com.sm.engine.service;

import com.sm.engine.domain.UserAttributesRequest;

import java.util.List;
import java.util.Map;

/**
 * The ldap service interface.
 */
public interface LdapService {

    /**
     * Authenticate with username/password.
     *
     * @param username the username
     * @param password the password
     * @return true if username/password are valid
     */
    boolean authenticate(String username, String password);

    /**
     * Find user dn by user name.
     *
     * @param username the username
     * @return the match ldap user dn and null if not found.
     */
    String findUserDN(String username);

    /**
     * List ldap users.
     *
     * @return the ldap users.
     */
    List<String> listUsers();

    /**
     * Evaluate rule.
     *
     * @param rule the rule.
     * @return true if rule is valid otherwise false.
     */
    boolean evaluateRule(String rule);

    /**
     * Get user attributes.
     *
     * @param request the user attributes request.
     * @return the user attributes.
     */
    Map<String, Object> getUserAttributes(UserAttributesRequest request);
}
