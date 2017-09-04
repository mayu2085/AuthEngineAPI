package com.sm.engine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sm.engine.domain.Header;
import com.sm.engine.domain.HeaderEvaluateResult;
import com.sm.engine.domain.HeaderType;
import com.sm.engine.domain.Policy;
import com.sm.engine.domain.Rule;
import com.sm.engine.domain.UserAttributesRequest;
import com.sm.engine.domain.support.NameSearchCriteria;
import com.sm.engine.domain.support.PolicySearchCriteria;
import com.sm.engine.domain.support.SearchResult;
import com.sm.engine.exception.NotFoundException;
import com.sm.engine.utils.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;
import static org.springframework.data.domain.ExampleMatcher.matching;

/**
 * The service provides operations to Header document.
 */
@Service
@Transactional
public class HeaderService extends BaseService<Header, NameSearchCriteria> {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(HeaderService.class);

    /**
     * The comma.
     */
    private static final String COMMA = ",";

    /**
     * The ldap configuration service.
     */
    @Autowired
    private LdapConfigurationService ldapConfigurationService;

    /**
     * The ldap attribute service.
     */
    @Autowired
    private LdapAttributeService ldapAttributeService;

    /**
     * The policy service
     */
    @Autowired
    private PolicyService policyService;

    /**
     * Validate all documents referenced by the specified document. Concrete classes should overwrite
     * if necessary.
     * It will also create or update referenced documents if necessary.
     *
     * @param document the document to validate/populate
     * @throws IllegalArgumentException if there's any error
     */
    @Override
    protected void validateAndPopulateReferences(Header document) {
        Helper.validateHeader(document);
    }

    /**
     * Creates the search example instance.
     *
     * @param criteria the search criteria
     * @return the example instance
     */
    @Override
    protected Example<Header> createSearchExample(NameSearchCriteria criteria) {
        Header example = new Header();
        example.setHeaderName(criteria.getName());
        return Example.of(example, matching().withMatcher("headerName", contains()));
    }

    /**
     * Gets the headers evaluate results for the specified username.
     *
     * @param username the username
     * @param moduleId the id of module
     * @return the headers evaluate result
     */
    @Transactional(readOnly = true)
    public List<HeaderEvaluateResult> evaluate(String username, String moduleId) {
        Helper.checkNullOrEmpty(username, "username");
        LdapService ldapService = Helper.buildLdapService(ldapConfigurationService);
        String userDN = ldapService.findUserDN(username);
        if (Helper.isNullOrEmpty(userDN)) {
            throw new NotFoundException("There is no ldap user with username '" + username + "' found");
        }
        List<String> headerNames = new ArrayList<>();
        List<HeaderEvaluateResult> result = new ArrayList<>();
        PolicySearchCriteria policySearchCriteria = new PolicySearchCriteria();
        policySearchCriteria.setModuleId(moduleId);
        policySearchCriteria.setEnabled(Boolean.TRUE);
        SearchResult<Policy> policyResult = policyService.search(policySearchCriteria, null);
        List<Policy> policies = policyResult.getRecords();
        Set<Header> dynamicHeaders = new HashSet<>();
        if (policies != null) {
            for (Policy policy : policies) {
                List<Rule> rules = policy.getRules();
                if (rules != null) {
                    for (Rule rule : rules) {
                        if (rule != null && rule.getRuleInfo() != null && rule.getHeader() != null) {
                            String ldapRule = rule.getRuleInfo().stream()
                                    .map(r -> r.getName() + "=" + r.getValue())
                                    .collect(Collectors.joining(COMMA));
                            if (!ldapService.evaluateRule(ldapRule)) {
                                continue;
                            }
                            Header header = rule.getHeader();
                            String headerName = header.getHeaderName();
                            if (headerNames.contains(headerName)) {
                                LOG.warn("Already exist static or dynamic header with name '{}'", headerName);
                                continue;
                            }
                            headerNames.add(headerName);
                            if (HeaderType.STATIC.equals(header.getType())) {
                                // static headers
                                result.add(new HeaderEvaluateResult(headerName, header.getValue()));
                            } else if (HeaderType.DYNAMIC.equals(header.getType())) {
                                // dynamic headers from ldap
                                dynamicHeaders.add(header);
                            }
                        }
                    }
                }
            }
            if (!dynamicHeaders.isEmpty()) {
                // speed up by call once for all dynamic headers for same user dn
                String[] attributes = dynamicHeaders.stream()
                        .map(Header::getValue)
                        //.filter(ldapAttributeService.getAllNames()::contains)// must be enabled attribute in database
                        .toArray(String[]::new);
                UserAttributesRequest attributesRequest = new UserAttributesRequest();
                attributesRequest.setUserDN(userDN);
                attributesRequest.setAttributes(attributes);
                Map<String, Object> attributeValues = ldapService.getUserAttributes(attributesRequest);
                for (Header dynamicHeader : dynamicHeaders) {
                    if (attributeValues.containsKey(dynamicHeader.getValue())) {
                        Object attributeValue = attributeValues.get(dynamicHeader.getValue());
                        String value = null;
                        if (attributeValue==null){
                            value = "null";
                        }
                        else if (attributeValue instanceof String) {
                            value = (String) attributeValue;
                        } else {
                            // will join array of string by comma
                            value = String.join(COMMA, (String[]) attributeValue);
                        }
                        result.add(new HeaderEvaluateResult(dynamicHeader.getHeaderName(),
                                value));
                    }
                }
            }
        }
        return result;
    }
}
