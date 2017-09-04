package com.sm.engine.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sm.engine.TestData;
import com.sm.engine.config.TestLdapConfig;
import com.sm.engine.domain.Header;
import com.sm.engine.domain.HeaderEvaluateResult;
import com.sm.engine.domain.HeaderType;
import com.sm.engine.domain.Policy;
import com.sm.engine.domain.Role;
import com.sm.engine.domain.support.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HeaderController tests.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class HeaderControllerTest extends BaseControllerTest<Header> {
    /**
     * The test ldap configurations.
     */
    @Autowired
    private TestLdapConfig testLdapConfig;

    /**
     * All test documents.
     */
    private List<Header> allDocuments = TestData.generateHeaders();

    /**
     * Creates a new instance.
     */
    public HeaderControllerTest() {
        baseUrl = "/headers";
        searchWithPaginationQuery = "?page=1&size=2&sort=headerName,desc";
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
     * Negative create tests, invalid value.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void create400_InvalidValue() throws Exception {
        Header original = getDocumentForCreateTest();
        original.setValue(null);
        String originalJson = objectMapper.writeValueAsString(original);
        mockMvc //
                .perform( //
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(400));
        original.setType(HeaderType.STATIC);
        original.setValue("Wrong static header value");
        originalJson = objectMapper.writeValueAsString(original);
        mockMvc //
                .perform( //
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(400));
    }


    /**
     * Test evaluate endpoint
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void evaluate() throws Exception {
        mongoOperations.insert(TestData.createValidLdapConfiguration(testLdapConfig));
        mongoOperations.insertAll(TestData.createValidLdapHeaders());
        Policy policy = TestData.createValidLdapPolicy();
        mongoOperations.insert(policy);
        mongoOperations.insertAll(TestData.createValidLdapAttributes());
        validateEvaluateResult(objectMapper.readValue(mockMvc
                .perform(
                        get(baseUrl + "/evaluate/admin_user1")
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andReturn().getResponse().getContentAsString(), new TypeReference<List<HeaderEvaluateResult>>() {}));

        // check 404 for not found usernmae
        mockMvc
                .perform(
                        get(baseUrl + "/evaluate/notexist")
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().isNotFound());

        // check result for not found moduleId
        mockMvc
                .perform(
                        get(baseUrl + "/evaluate/admin_user1?moduleId=notexist")
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        // validate same response if custom module id
        validateEvaluateResult(objectMapper.readValue(mockMvc
                .perform(
                        get(baseUrl + "/evaluate/admin_user1?moduleId=" + policy.getModule().getId())
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andReturn().getResponse().getContentAsString(), new TypeReference<List<HeaderEvaluateResult>>() {}));
    }

    /**
     * Validate evaluate result.
     * @param values the evaluate result
     */
    private static void  validateEvaluateResult(List<HeaderEvaluateResult> values){
        assertEquals(5, values.size());
        Map<String, String> result = values.stream()
                .collect(Collectors.toMap(HeaderEvaluateResult::getName, HeaderEvaluateResult::getValue));
        assertTrue(result.containsKey("statictrue"));
        assertEquals("True", result.get("statictrue"));
        assertTrue(result.containsKey("staticfalse"));
        assertEquals("False", result.get("staticfalse"));
        assertTrue(result.containsKey("dynamic_objectclass"));
        assertEquals("top,person,organizationalPerson,inetOrgPerson", result.get("dynamic_objectclass"));
        assertTrue(result.containsKey("dynamic_uid"));
        assertEquals("admin_user1", result.get("dynamic_uid"));
        assertTrue(result.containsKey("dynamic_sn"));
        assertEquals("Admin User1", result.get("dynamic_sn"));
    }

    /**
     * Gets expected result for search tests.
     *
     * @return the expected search result
     */
    @Override
    protected SearchResult<Header> getExpectedResultForSearchTests() {
        return new SearchResult<>(allDocuments.size(), allDocuments);
    }

    /**
     * Gets expected result for search with pagination tests.
     *
     * @return the expected search result
     */
    @Override
    protected SearchResult<Header> getExpectedResultForSearchWithPaginationTests() {
        List<Header> documents = new ArrayList<>();
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
    protected SearchResult<Header> getExpectedResultForSearchWithPaginationAndCriteriaTest() {
        List<Header> documents = allDocuments.subList(0, 1);

        return new SearchResult<>(1, documents);
    }

    /**
     * Gets document for create test.
     *
     * @return the document
     */
    @Override
    protected Header getDocumentForCreateTest() {
        return TestData.createNewHeader();
    }

    /**
     * Gets document for update test.
     *
     * @return the document
     */
    @Override
    protected Header getDocumentForDuplicatedTest() {
        Header document = new Header();
        document.setHeaderName(allDocuments.get(0).getHeaderName());
        return document;
    }

    /**
     * Gets document for update test.
     *
     * @return the document
     */
    @Override
    protected Header getDocumentForUpdateTest() {
        Header document = TestData.generateHeaders().get(1);
        document.setType(HeaderType.DYNAMIC);
        document.setDescription("new description");
        document.setHeaderName("new name");
        document.setValue("new value");
        return document;
    }

    /**
     * Gets document for delete test.
     *
     * @return the document
     */
    @Override
    protected Header getDocumentForGetDeleteTest() {
        return allDocuments.get(1);
    }
}
