package com.sm.engine.service;

import com.sm.engine.domain.Header;
import com.sm.engine.domain.Module;
import com.sm.engine.domain.Policy;
import com.sm.engine.domain.Rule;
import com.sm.engine.domain.support.PolicySearchCriteria;
import com.sm.engine.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;
import static org.springframework.data.domain.ExampleMatcher.matching;

/**
 * The service provides operations to Policy document.
 */
@Service
@Transactional
public class PolicyService extends BaseService<Policy, PolicySearchCriteria> {

    /**
     * The module service.
     */
    @Autowired
    private ModuleService moduleService;

    /**
     * The header service.
     */
    @Autowired
    private HeaderService headerService;

    /**
     * Deletes policies by ids.
     *
     * @param ids the policy ids to delete
     */
    public void deleteByIds(List<String> ids) {
        for (String id : ids) {
            super.delete(id);
        }
    }

    /**
     * Validate all documents referenced by the specified document.
     *
     * @param document the document to validate
     * @throws IllegalArgumentException if there's any error
     */
    @Override
    protected void validateAndPopulateReferences(Policy document) {
        // create or update rules
        List<Rule> rules = document.getRules();
        if (rules != null) {
            if (rules.stream().anyMatch(Objects::isNull)) {
                throw new IllegalArgumentException("Rules of Policy must not include null item");
            }
            // name of rule could be null so should handle with special check
            if (rules.stream().filter(r -> r.getName() == null).count() +
                    rules.stream().filter(r -> r.getName() != null).map(Rule::getName).distinct().count()
                    != rules.size()) {
                throw new IllegalArgumentException("Rules of Policy should not include duplicated item");
            }
            rules.forEach(rule -> {
                validateList(rule.getRuleInfo(), "ruleInfo");
                rule.setHeader(headerService.createOrUpdateDocument(rule.getHeader()));
            });

            if (rules.stream().map(Rule::getHeader).map(Header::getHeaderName).distinct().count() != rules.size()) {
                throw new IllegalArgumentException("Rules of Policy should not include duplicated header");
            }
        }
        // create or update module
        Module dbModule = moduleService.createOrUpdateDocument(document.getModule());
        document.setModule(dbModule);
    }

    /**
     * Creates the search example instance.
     *
     * @param criteria the search criteria
     * @return the example instance
     */
    @Override
    protected Example<Policy> createSearchExample(PolicySearchCriteria criteria) {
        Policy example = new Policy();
        example.setName(criteria.getName());
        example.setEnabled(criteria.getEnabled());
        if (!Helper.isNullOrEmpty(criteria.getModuleId())) {
            Module module = new Module();
            module.setId(criteria.getModuleId());
            example.setModule(module);
        }
        return Example.of(example, matching().withMatcher("name", contains()));
    }
}
