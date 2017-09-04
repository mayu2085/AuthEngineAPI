package com.sm.engine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sm.engine.TestApplication;
import com.sm.engine.TestData;
import com.sm.engine.domain.ActivityLog;
import com.sm.engine.domain.Header;
import com.sm.engine.domain.IdentifiableDocument;
import com.sm.engine.domain.LdapAttribute;
import com.sm.engine.domain.LdapConfiguration;
import com.sm.engine.domain.Module;
import com.sm.engine.domain.OperationType;
import com.sm.engine.domain.Policy;
import com.sm.engine.domain.Role;
import com.sm.engine.domain.System;
import com.sm.engine.domain.User;
import com.sm.engine.domain.support.SearchResult;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Base class for all CRUD controller tests.
 */
@ContextConfiguration(classes = {TestApplication.class})
@WebAppConfiguration
public abstract class BaseControllerTest<T extends IdentifiableDocument> {

    /**
     * The web application context.
     */
    @Autowired
    private WebApplicationContext context;

    /**
     * The Mongo operations instance.
     */
    @Autowired
    protected MongoOperations mongoOperations;

    /**
     * The mock MVC.
     */
    protected static MockMvc mockMvc;

    /**
     * The base url.
     */
    protected String baseUrl;

    /**
     * The search with pagination query.
     */
    protected String searchWithPaginationQuery;

    /**
     * The search with pagination and criteria query.
     */
    protected String searchWithPaginationAndCriteriaQuery;

    /**
     * The invalid search query.
     */
    protected String search400Query;

    /**
     * The object mapper.
     */
    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * Run before each test to populate test data and initialize the mock MVC.
     */
    @Before
    public void before() {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        }

        removeTestData();
        insertTestData();
    }

    /**
     * Remove test data.
     */
    private void removeTestData() {
        mongoOperations.remove(new Query(), ActivityLog.class);

        mongoOperations.remove(new Query(), User.class);
        mongoOperations.remove(new Query(), LdapConfiguration.class);
        mongoOperations.remove(new Query(), LdapAttribute.class);
        mongoOperations.remove(new Query(), Policy.class);
        mongoOperations.remove(new Query(), Module.class);
        mongoOperations.remove(new Query(), System.class);

        mongoOperations.remove(new Query(), Header.class);
    }

    /**
     * Inserts test data.
     */
    private void insertTestData() {
        mongoOperations.insert(TestData.generateUsers(), User.class);
        mongoOperations.insert(TestData.generateLdapConfigurations(), LdapConfiguration.class);

        mongoOperations.insert(TestData.generateHeaders(), Header.class);

        mongoOperations.insert(TestData.generateSystems(), System.class);
        mongoOperations.insert(TestData.generateModules(), Module.class);
        mongoOperations.insert(TestData.generatePolicies(), Policy.class);
    }

    /**
     * 401 - Unauthorized tests.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void status401() throws Exception {
        mockMvc.perform(get(baseUrl)).andExpect(status().is(401));
        mockMvc.perform(post(baseUrl)).andExpect(status().is(401));
        mockMvc.perform(put(baseUrl)).andExpect(status().is(401));
        mockMvc.perform(delete(baseUrl)).andExpect(status().is(401));
    }

    /**
     * Positive search test.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void search() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(getExpectedResultForSearchTests());

        mockMvc
                .perform(
                        get(baseUrl)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson, true));
    }

    /**
     * Positive search test with pagination.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void searchWithPagination() throws Exception {
        String expectedJson =
                objectMapper.writeValueAsString(getExpectedResultForSearchWithPaginationTests());

        mockMvc
                .perform(
                        get(baseUrl + searchWithPaginationQuery)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson, true));
    }

    /**
     * Positive search test with pagination and criteria.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void searchWithPaginationAndCriteria() throws Exception {
        String expectedJson =
                objectMapper.writeValueAsString(getExpectedResultForSearchWithPaginationAndCriteriaTest());

        mockMvc
                .perform(
                        get(baseUrl + searchWithPaginationAndCriteriaQuery)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson, true));
    }

    /**
     * Negative search test.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void search400() throws Exception {
        if (search400Query == null) {
            return;
        }

        mockMvc
                .perform(
                        get(baseUrl + search400Query).
                                with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(400));
    }

    /**
     * Test create endpoint
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void create() throws Exception {
        T original = getDocumentForCreateTest();
        String originalJson = objectMapper.writeValueAsString(original);

        //  Create
        mockMvc
                .perform(
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().isOk())
                .andExpect(content().json(originalJson, true));

        //  Check DB
        T dbDocument = (T) mongoOperations
                .findOne(Query.query(Criteria.where("id").is(original.getId())), original.getClass());
        assertNotNull(dbDocument);
        assertEquals(objectMapper.writeValueAsString(original),
                objectMapper.writeValueAsString(dbDocument));

        //  New Activity Log added
        List<ActivityLog> activityLogs = mongoOperations.findAll(ActivityLog.class)
                .stream()
                .filter(a-> OperationType.Create.equals(a.getOperationType()) &&
                        dbDocument.getClass().getSimpleName().equals(a.getDocumentType()))
                .collect(Collectors.toList());
        assertTrue(activityLogs.size() == 1);
        assertEquals("admin_user1", activityLogs.get(0).getOperatedBy());
        assertNotNull(activityLogs.get(0).getDescription());
        assertNotNull(activityLogs.get(0).getCreatedAt());
    }

    /**
     * Negative create test.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void create400() throws Exception {
        mockMvc
                .perform(
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content("{}")
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(400));
    }

    /**
     * Negative create test, duplicated.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void create400_Duplicated() throws Exception {
        T original = getDocumentForDuplicatedTest();
        if (original == null) {
            return;
        }
        String originalJson = objectMapper.writeValueAsString(original);

        // Create
        mockMvc
                .perform(
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(400));
    }

    /**
     * Test update endpoint
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void update() throws Exception {
        T original = getDocumentForUpdateTest();
        String originalJson = objectMapper.writeValueAsString(original);

        //  Update
        mockMvc
                .perform(
                        put(baseUrl + "/" + original.getId()).contentType(MediaType.APPLICATION_JSON)
                                .content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().isOk())
                .andExpect(content().json(originalJson, true));

        // Check DB
        T dbDocument = (T) mongoOperations
                .findOne(Query.query(Criteria.where("id").is(original.getId())), original.getClass());
        assertNotNull(dbDocument);
        assertEquals(objectMapper.writeValueAsString(original),
                objectMapper.writeValueAsString(dbDocument));

        //  New Activity Log added
        List<ActivityLog> activityLogs = mongoOperations.findAll(ActivityLog.class)
                .stream()
                .filter(a-> OperationType.Change.equals(a.getOperationType()) &&
                        dbDocument.getClass().getSimpleName().equals(a.getDocumentType()))
                .collect(Collectors.toList());
        assertTrue(activityLogs.size() == 1);
        assertEquals("admin_user1", activityLogs.get(0).getOperatedBy());
        assertNotNull(activityLogs.get(0).getDescription());
        assertNotNull(activityLogs.get(0).getCreatedAt());
    }

    /**
     * Negative update test.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void update400() throws Exception {
        T original = getDocumentForUpdateTest();

        mockMvc
                .perform(
                        put(baseUrl + "/" + original.getId()).contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(400));
    }

    /**
     * Negative update test, duplicated.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void update400_Duplicated() throws Exception {
        T original = getDocumentForUpdateTest();
        T originalDuplicated = getDocumentForDuplicatedTest();
        if (originalDuplicated == null) {
            return;
        }
        originalDuplicated.setId(original.getId());
        String originalJson = objectMapper.writeValueAsString(originalDuplicated);

        mockMvc
                .perform(
                        put(baseUrl + "/" + original.getId()).contentType(MediaType.APPLICATION_JSON)
                                .content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(400));
    }

    /**
     * Negative update test, not found.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void update404() throws Exception {
        T original = getDocumentForUpdateTest();
        original.setId("577e8a900000000001100000");
        String originalJson = objectMapper.writeValueAsString(original);

        mockMvc
                .perform(
                        put(baseUrl + "/" + original.getId()).contentType(MediaType.APPLICATION_JSON)
                                .content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(404));
    }

    /**
     * Positive get by id test.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void getById() throws Exception {
        T existing = getDocumentForGetDeleteTest();
        String expectedJson = objectMapper.writeValueAsString(existing);

        mockMvc
                .perform(
                        get(baseUrl + "/" + existing.getId())
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson, true));
    }

    /**
     * Negative get by id test, not found.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void getById404() throws Exception {
        mockMvc
                .perform(
                        get(baseUrl + "/577e8a800000000000000000")
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(404));
    }

    /**
     * Positive delete by id test.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void deleteById() throws Exception {
        T existing = getDocumentForGetDeleteTest();

        mockMvc
                .perform(
                        delete(baseUrl + "/" + existing.getId())
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().isOk());

        // Check DB
        boolean existed = mongoOperations.exists(Query.query(Criteria.where("id").is(existing.getId())),
                existing.getClass());
        assertFalse(existed);

        // New Activity Log added
        List<ActivityLog> activityLogs = mongoOperations.findAll(ActivityLog.class);
        assertTrue(activityLogs.size() == 1);
        assertEquals(existing.getClass().getSimpleName(), activityLogs.get(0).getDocumentType());
        assertEquals("admin_user1", activityLogs.get(0).getOperatedBy());
        assertEquals(OperationType.Delete, activityLogs.get(0).getOperationType());
        assertNotNull(activityLogs.get(0).getDescription());
        assertNotNull(activityLogs.get(0).getCreatedAt());
    }

    /**
     * Negative get by id test, not found.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void deleteById404() throws Exception {
        mockMvc
                .perform(
                        delete(baseUrl + "/577e8a900000000001100000")
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(404));
    }

    /**
     * Gets expected result for search tests.
     *
     * @return the expected result
     */
    protected abstract SearchResult<T> getExpectedResultForSearchTests();

    /**
     * Gets expected result for search with pagination tests.
     *
     * @return the expected result
     */
    protected abstract SearchResult<T> getExpectedResultForSearchWithPaginationTests();

    /**
     * Gets expected result for search with pagination and criteria tests.
     *
     * @return the expected result
     */
    protected abstract SearchResult<T> getExpectedResultForSearchWithPaginationAndCriteriaTest();

    /**
     * Gets document for create tests.
     *
     * @return the expected result
     */
    protected abstract T getDocumentForCreateTest();

    /**
     * Gets document for duplicated error tests.
     *
     * @return the expected result
     */
    protected abstract T getDocumentForDuplicatedTest();

    /**
     * Gets document for update tests.
     *
     * @return the expected result
     */
    protected abstract T getDocumentForUpdateTest();

    /**
     * Gets document for delete tests.
     *
     * @return the expected result
     */
    protected abstract T getDocumentForGetDeleteTest();
}
