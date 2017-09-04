package com.sm.engine.controller;

import com.sm.engine.TestData;
import com.sm.engine.domain.Role;
import com.sm.engine.domain.System;
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
 * SystemController tests.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class SystemControllerTest extends BaseControllerTest<System> {

    /**
     * All documents.
     */
    private List<System> allDocuments = TestData.generateSystems();

    /**
     * Creates a new instance.
     */
    public SystemControllerTest() {
        baseUrl = "/systems";
        searchWithPaginationQuery = "?page=1&size=2&sort=name,desc";
        searchWithPaginationAndCriteriaQuery = "?page=0&size=1&sort=description,asc&name=name 1";
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
     * @return the expected result
     */
    @Override
    protected SearchResult<System> getExpectedResultForSearchTests() {
        return new SearchResult<>(allDocuments.size(), allDocuments);
    }

    /**
     * Gets expected result for search with pagination tests.
     *
     * @return the expected result
     */
    @Override
    protected SearchResult<System> getExpectedResultForSearchWithPaginationTests() {
        List<System> documents = new ArrayList<>();
        documents.add(allDocuments.get(1));
        documents.add(allDocuments.get(0));

        return new SearchResult<>(allDocuments.size(), documents);
    }

    /**
     * Gets expected result for search with pagination and criteria tests.
     *
     * @return the expected result
     */
    @Override
    protected SearchResult<System> getExpectedResultForSearchWithPaginationAndCriteriaTest() {
        List<System> documents = allDocuments.subList(0, 1);

        return new SearchResult<>(1, documents);
    }

    /**
     * Gets document for create test.
     *
     * @return the document
     */
    @Override
    protected System getDocumentForCreateTest() {
        return TestData.createNewSystem();
    }

    /**
     * Gets document for duplicated test.
     *
     * @return the document
     */
    @Override
    protected System getDocumentForDuplicatedTest() {
        System document = new System();
        document.setName(allDocuments.get(0).getName());
        return document;
    }

    /**
     * Gets document for update test.
     *
     * @return the document
     */
    @Override
    protected System getDocumentForUpdateTest() {
        System document = TestData.generateSystems().get(1);

        document.setDescription("new description");
        document.setName("new name");

        return document;
    }

    /**
     * Gets document for delete test.
     *
     * @return the document
     */
    @Override
    protected System getDocumentForGetDeleteTest() {
        return allDocuments.get(1);
    }
}
