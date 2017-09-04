package com.sm.engine.service.Impl;

import com.sm.engine.config.LdapConfig;
import com.sm.engine.domain.UserAttributesRequest;
import com.sm.engine.service.LdapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.core.support.CountNameClassPairCallbackHandler;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.EqualsFilter;

import javax.naming.directory.SearchControls;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

/**
 * The ldap service implement the interface.
 */
public class LdapServiceImpl implements LdapService {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(LdapServiceImpl.class);

    /**
     * The ldap template.
     */
    private LdapTemplate template;

    /**
     * The ldap config.
     */
    private LdapConfig ldapConfig;

    /**
     * The ldap service constructor to inject ldap config and build ldap template.
     *
     * @param ldapConfig the ldap config
     */
    public LdapServiceImpl(LdapConfig ldapConfig) {
        this.ldapConfig = ldapConfig;
        LdapContextSource cs = new LdapContextSource();
        cs.setCacheEnvironmentProperties(false);
        cs.setUrl(ldapConfig.getUrl());
        cs.setBase(ldapConfig.getRoot());
        cs.setUserDn(ldapConfig.getUserDn());
        cs.setPassword(ldapConfig.getPassword());
        cs.afterPropertiesSet();
        template = new LdapTemplate(cs);
        template.setDefaultSearchScope(ldapConfig.getSearchScope().getId());
    }

    /**
     * Authenticate with username/password.
     *
     * @param username the username
     * @param password the password
     * @return true if username/password are valid
     */
    @Override
    public boolean authenticate(String username, String password) {
        return template.authenticate(ldapConfig.getUserSearchBase(), buildUsernameFilter(username), password);
    }


    /**
     * Find user dn by user name.
     *
     * @param username the username
     * @return the match ldap user dn and null if not found.
     */
    @Override
    public String findUserDN(String username) {
        try {
            return template.searchForObject(ldapConfig.getUserSearchBase(),
                    buildUsernameFilter(username), ctx -> {
                        DirContextAdapter adapter = (DirContextAdapter) ctx;
                        return adapter.getDn().toString();
                    });
        } catch (IncorrectResultSizeDataAccessException ex) {
            LOG.error("Error happened during finding user dn", ex);
            return null;
        }
    }


    /**
     * List ldap users.
     *
     * @return the ldap users.
     */
    @Override
    public List<String> listUsers() {
        String attribute = ldapConfig.getUserAttribute();
        return template.search(
                query().attributes(attribute)
                        .base(ldapConfig.getUserSearchBase())
                        .where(attribute).isPresent(),
                (AttributesMapper<String>) attrs -> (String) attrs.get(attribute).get());
    }

    /**
     * Evaluate rule.
     *
     * @param rule the rule.
     * @return true if rule is valid otherwise false.
     */
    @Override
    public boolean evaluateRule(String rule) {
        try {
            SearchControls ctls = new SearchControls();
            // use count limit to speed up
            ctls.setCountLimit(1);
            ctls.setSearchScope(ldapConfig.getSearchScope().getId());
            CountNameClassPairCallbackHandler handler = new CountNameClassPairCallbackHandler();
            template.search(rule, "objectclass=*", ctls, handler);
            return handler.getNoOfRows() > 0;
        } catch (NamingException ex) {
            LOG.error("Error happened during evaluating rule", ex);
            return false;
        }
    }

    /**
     * Get user attributes.
     *
     * @param request the user attributes request.
     * @return the user attributes.
     */
    @Override
    public Map<String, Object> getUserAttributes(UserAttributesRequest request) {
        return template.lookup(request.getUserDN(), request.getAttributes(), new AbstractContextMapper<Map<String, Object>>() {
            @Override
            protected Map<String, Object> doMapFromContext(DirContextOperations ctx) {
                Map<String, Object> result = new HashMap<>();
                for (String key : request.getAttributes()) {
                    String[] values = ctx.getStringAttributes(key);
                    if (values != null && values.length > 1) {
                        // real multi values
                        result.put(key, values);
                    } else {
                        // single value
                        result.put(key, ctx.getStringAttribute(key));
                    }
                }
                return result;
            }
        });
    }

    /**
     * Build username filter.
     *
     * @param username the username
     * @return the username filter.
     */
    private String buildUsernameFilter(String username) {
        return new EqualsFilter(ldapConfig.getUserAttribute(), username).encode();
    }
}
