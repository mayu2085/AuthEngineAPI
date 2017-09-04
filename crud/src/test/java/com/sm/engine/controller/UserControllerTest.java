package com.sm.engine.controller;

import com.sm.engine.TestData;
import com.sm.engine.domain.Role;
import com.sm.engine.domain.User;
import com.sm.engine.domain.support.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserController tests.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class UserControllerTest extends BaseControllerTest<User> {

    /**
     * All documents.
     */
    private List<User> allDocuments = TestData.generateUsers();

    /**
     * Creates a new instance.
     */
    public UserControllerTest() {
        baseUrl = "/users";
        searchWithPaginationQuery = "?page=0&size=2&sort=username,asc";
        searchWithPaginationAndCriteriaQuery =
                "?page=0&size=2&sort=username,desc&role=Admin&username=user1";
        search400Query = "?page=0&size=2&sort=username,desc&role=invalid&username=user1";
    }

    /**
     * 403 Forbidden tests.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void status403() throws Exception {
        mockMvc.perform(get(baseUrl).with(user("user1").password("secret").roles(Role.RW.toString())))
                .andExpect(status().is(403));
        mockMvc.perform(get(baseUrl).with(user("user2").password("secret").roles(Role.RO.toString())))
                .andExpect(status().is(403));

        mockMvc.perform(post(baseUrl).with(user("user1").password("secret").roles(Role.RW.toString())))
                .andExpect(status().is(403));
        mockMvc.perform(post(baseUrl).with(user("user2").password("secret").roles(Role.RO.toString())))
                .andExpect(status().is(403));

        mockMvc.perform(put(baseUrl).with(user("user1").password("secret").roles(Role.RW.toString())))
                .andExpect(status().is(403));
        mockMvc.perform(put(baseUrl).with(user("user2").password("secret").roles(Role.RO.toString())))
                .andExpect(status().is(403));

        mockMvc
                .perform(delete(baseUrl).with(user("user1").password("secret").roles(Role.RW.toString())))
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
    protected SearchResult<User> getExpectedResultForSearchTests() {
        // All users
        SearchResult<User> searchResult = new SearchResult<>(allDocuments.size(), allDocuments);

        return searchResult;
    }

    /**
     * Gets expected result for search with pagination tests.
     *
     * @return the expected result
     */
    @Override
    protected SearchResult<User> getExpectedResultForSearchWithPaginationTests() {
        // -> admin_user1, admin_user2
        List<User> users = allDocuments.subList(4, 6);

        SearchResult<User> searchResult = new SearchResult<>(allDocuments.size(), users);

        return searchResult;
    }

    /**
     * Gets expected result for search with pagination and criteria tests.
     *
     * @return the expected result
     */
    @Override
    protected SearchResult<User> getExpectedResultForSearchWithPaginationAndCriteriaTest() {
        // -> admin_user1
        List<User> users = allDocuments.subList(4, 5);

        SearchResult<User> searchResult = new SearchResult<>(1, users);

        return searchResult;
    }

    /**
     * Gets document for create tests.
     *
     * @return the document
     */
    @Override
    protected User getDocumentForCreateTest() {
        return TestData.createNewUser();
    }

    /**
     * Gets document for duplicated tests.
     *
     * @return the document
     */
    @Override
    protected User getDocumentForDuplicatedTest() {
        return new User(allDocuments.get(0).getUsername(), Role.Admin, true);
    }

    /**
     * Gets document for update tests.
     *
     * @return the document
     */
    @Override
    protected User getDocumentForUpdateTest() {
        User user = TestData.generateUsers().get(1);

        user.setEnabled(!user.getEnabled());
        user.setRole(Role.RO);
        user.setUsername("new " + user.getUsername());

        return user;
    }

    /**
     * Gets document for delete tests.
     *
     * @return the document
     */
    @Override
    protected User getDocumentForGetDeleteTest() {
        return allDocuments.get(1);
    }
}
