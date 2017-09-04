package com.sm.engine.utils;

import com.sm.engine.config.LdapConfig;
import com.sm.engine.domain.Header;
import com.sm.engine.domain.HeaderType;
import com.sm.engine.domain.LdapConfiguration;
import com.sm.engine.domain.User;
import com.sm.engine.domain.support.LdapConfigurationSearchCriteria;
import com.sm.engine.domain.support.SearchResult;
import com.sm.engine.exception.NotFoundException;
import com.sm.engine.service.Impl.LdapServiceImpl;
import com.sm.engine.service.LdapConfigurationService;
import com.sm.engine.service.LdapService;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ldap.query.SearchScope;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static lombok.AccessLevel.PRIVATE;

/**
 * This class provides help methods used in this application.
 */
@NoArgsConstructor(access = PRIVATE)
public class Helper {
    /**
     * The true value for static header type
     */
    private static final String TRUE = "True";

    /**
     * The false value for static header type
     */
    private static final String FALSE = "False";

    /**
     * It checks whether a given string is null or empty.
     *
     * @param str the string to be checked
     * @return true if a given string is null or empty
     */
    public static boolean isNullOrEmpty(String str) throws IllegalArgumentException {
        return str == null || str.trim().isEmpty();
    }

    /**
     * It checks whether a given string is null or empty.
     *
     * @param str  the string to be checked
     * @param name the name of the string, used in the exception message
     * @throws IllegalArgumentException the exception thrown when the given string is null or empty
     */
    public static void checkNullOrEmpty(String str, String name) throws IllegalArgumentException {
        if (isNullOrEmpty(str)) {
            throw new IllegalArgumentException(String.format("%s should be valid string(not null and not empty)", name));
        }
    }

    /**
     * Check valid user.
     *
     * @param user     the user
     * @param username the username
     * @throws UsernameNotFoundException throws if user is null
     * @throws DisabledException         throws if user is disabled
     */
    public static void checkUser(User user, String username) {
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User with name '%s' not found", username));
        }
        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new DisabledException(String.format("User with name '%s' is disabled", username));
        }
    }

    /**
     * Build ldap service with enabled ldap configurations.
     *
     * @param ldapConfigurationService the ldap configuration service
     * @return the ldap service with enabled ldap configurations.
     * @throws NotFoundException throws if no enabled ldap configuration.
     */
    public static LdapService buildLdapService(LdapConfigurationService ldapConfigurationService) {
        LdapConfigurationSearchCriteria criteria = new LdapConfigurationSearchCriteria();
        criteria.setEnabled(true);
        // use latest enabled ldap configuration
        SearchResult<LdapConfiguration> configs = ldapConfigurationService.search(criteria,
                new PageRequest(0, 1, Sort.Direction.DESC, "lastModifiedAt"));
        if (configs.getRecords().isEmpty()) {
            throw new NotFoundException("There is no enabled ldap configuration");
        }

        LdapConfiguration config = configs.getRecords().get(0);
        LdapConfig conf = new LdapConfig();
        BeanUtils.copyProperties(config, conf);
        conf.setSearchScope(SearchScope.SUBTREE);
        return new LdapServiceImpl(conf);
    }

    /**
     * Validate header.
     *
     * @param header the header to validate.
     * @throws IllegalArgumentException if there's any validation error
     */
    public static void validateHeader(Header header) {
        if (HeaderType.STATIC.equals(header.getType())) {
            String value = header.getValue();
            if (!TRUE.equals(value) && !FALSE.equals(value)) {
                throw new IllegalArgumentException(
                        String.format("Header value %s must be %s or %s for static type header", value, TRUE, FALSE));
            }
        }
    }
}
