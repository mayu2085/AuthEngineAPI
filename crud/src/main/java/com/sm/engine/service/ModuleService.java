package com.sm.engine.service;

import com.sm.engine.domain.Module;
import com.sm.engine.domain.System;
import com.sm.engine.domain.support.ModuleSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;
import static org.springframework.data.domain.ExampleMatcher.matching;

/**
 * The service provides operations to Module document.
 */
@Service
@Transactional
public class ModuleService extends BaseService<Module, ModuleSearchCriteria> {

    /**
     * The system service.
     */
    @Autowired
    private SystemService systemService;

    /**
     * Validate all documents referenced by the specified document. Concrete classes should overwrite
     * if necessary.
     * It will also create or update referenced documents if necessary.
     *
     * @param document the document to validate/populate
     * @throws IllegalArgumentException if there's any error
     */
    @Override
    protected void validateAndPopulateReferences(Module document) {
        //create or update system
        document.setSystem(systemService.createOrUpdateDocument(document.getSystem()));
    }

    /**
     * Creates the search example instance.
     *
     * @param criteria the search criteria
     * @return the example instance
     */
    @Override
    protected Example<Module> createSearchExample(ModuleSearchCriteria criteria) {
        Module example = new Module();
        example.setName(criteria.getName());

        if (!StringUtils.isEmpty(criteria.getSystemId())) {
            System system = new System();
            system.setId(criteria.getSystemId());

            example.setSystem(system);
        }

        return Example.of(example, matching().withMatcher("name", contains()));
    }
}
