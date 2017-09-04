package com.sm.engine.service;

import com.sm.engine.domain.System;
import com.sm.engine.domain.support.NameSearchCriteria;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;
import static org.springframework.data.domain.ExampleMatcher.matching;

/**
 * The service provides operations to System document.
 */
@Service
@Transactional
public class SystemService extends BaseService<System, NameSearchCriteria> {

    /**
     * Creates the search example instance.
     *
     * @param criteria the search criteria
     * @return the example instance
     */
    @Override
    protected Example<System> createSearchExample(NameSearchCriteria criteria) {
        System systemExample = new System();
        systemExample.setName(criteria.getName());

        return Example.of(systemExample, matching().withMatcher("name", contains()));
    }
}
