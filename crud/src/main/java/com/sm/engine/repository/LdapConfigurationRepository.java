package com.sm.engine.repository;

import com.sm.engine.domain.LdapConfiguration;
import org.springframework.stereotype.Repository;

/**
 * The LDAP configuration repository.
 */
@Repository
public interface LdapConfigurationRepository extends BaseRepository<LdapConfiguration> {
}
