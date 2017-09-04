package com.sm.engine.controller;

import com.sm.engine.TestData;
import com.sm.engine.domain.LdapConfiguration;
import com.sm.engine.domain.Role;
import com.sm.engine.domain.support.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * LdapConfigurationController tests.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class LdapConfigurationControllerTest extends BaseControllerTest<LdapConfiguration> {

    private List<LdapConfiguration> allDocuments = TestData.generateLdapConfigurations();

    /**
     * Create a new instance.
     */
    public LdapConfigurationControllerTest() {
        baseUrl = "/ldap-configurations";
        searchWithPaginationQuery = "?page=1&size=2&sort=name,desc";
        searchWithPaginationAndCriteriaQuery = "?page=0&size=1&sort=url,asc&name=name 1";
        search400Query = null; // Skip search400 test for this controller
    }

    /**
     * 403 Forbidden tests.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void status403() throws Exception {
        mockMvc.perform(post(baseUrl).with(user("user2").password("secret").roles(Role.RO.toString())))
                .andExpect(status().is(403));

        mockMvc.perform(put(baseUrl).with(user("user2").password("secret").roles(Role.RO.toString())))
                .andExpect(status().is(403));

        mockMvc
                .perform(delete(baseUrl).with(user("user2").password("secret").roles(Role.RO.toString())))
                .andExpect(status().is(403));
    }

    /**
     * Gets expected result for search tests.
     *
     * @return the expected search result
     */
    @Override
    protected SearchResult<LdapConfiguration> getExpectedResultForSearchTests() {
        return new SearchResult<>(allDocuments.size(), allDocuments);
    }

    /**
     * Gets expected result for search with pagination tests.
     *
     * @return the expected search result
     */
    @Override
    protected SearchResult<LdapConfiguration> getExpectedResultForSearchWithPaginationTests() {
        List<LdapConfiguration> documents = new ArrayList<>();
        documents.add(allDocuments.get(1));
        documents.add(allDocuments.get(0));

        return new SearchResult<>(allDocuments.size(), documents);
    }

    /**
     * Gets expected result for search with pagination and criteria tests.
     *
     * @return the expected search result
     */
    @Override
    protected SearchResult<LdapConfiguration> getExpectedResultForSearchWithPaginationAndCriteriaTest() {
        List<LdapConfiguration> documents = allDocuments.subList(0, 1);

        return new SearchResult<>(1, documents);
    }

    /**
     * Gets document for create tests.
     *
     * @return the document
     */
    @Override
    protected LdapConfiguration getDocumentForCreateTest() {
        return TestData.createNewLdapConfiguration();
    }

    /**
     * Gets document for duplicated tests.
     *
     * @return the document
     */
    @Override
    protected LdapConfiguration getDocumentForDuplicatedTest() {
        LdapConfiguration document = new LdapConfiguration();
        document.setName(allDocuments.get(0).getName());
        return document;
    }

    /**
     * Gets document for update tests.
     *
     * @return the document
     */
    @Override
    protected LdapConfiguration getDocumentForUpdateTest() {
        LdapConfiguration document = TestData.generateLdapConfigurations().get(1);

        document.setName("new name");
        document.setUrl("new url");
        document.setUserDn("new username");
        document.setPassword("new password");
        document.setRoot("new root");
        document.setUserSearchBase("new user search base");
        document.setUserAttribute("new user attribute");
        document.setEnabled(false);
        return document;
    }

    /**
     * Gets document for delete tests.
     *
     * @return the document
     */
    @Override
    protected LdapConfiguration getDocumentForGetDeleteTest() {
        return allDocuments.get(1);
    }
}
