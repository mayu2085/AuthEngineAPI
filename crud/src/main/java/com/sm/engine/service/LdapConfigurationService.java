package com.sm.engine.service;

import com.sm.engine.domain.LdapConfiguration;
import com.sm.engine.domain.support.LdapConfigurationSearchCriteria;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;
import static org.springframework.data.domain.ExampleMatcher.matching;

/**
 * The service provides operations to User document.
 */
@Service
@Transactional
public class LdapConfigurationService extends BaseService<LdapConfiguration, LdapConfigurationSearchCriteria> {

    /**
     * Creates the search example instance.
     *
     * @param criteria the search criteria
     * @return the example instance
     */
    @Override
    protected Example<LdapConfiguration> createSearchExample(LdapConfigurationSearchCriteria criteria) {
        LdapConfiguration searchExample = new LdapConfiguration();
        searchExample.setName(criteria.getName());
        searchExample.setEnabled(criteria.getEnabled());
        return Example.of(searchExample, matching().withMatcher("name", contains()));
    }
}
