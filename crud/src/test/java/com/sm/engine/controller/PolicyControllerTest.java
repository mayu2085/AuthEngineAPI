package com.sm.engine.controller;

import com.sm.engine.TestData;
import com.sm.engine.domain.ActivityLog;
import com.sm.engine.domain.Header;
import com.sm.engine.domain.Module;
import com.sm.engine.domain.OperationType;
import com.sm.engine.domain.Policy;
import com.sm.engine.domain.Role;
import com.sm.engine.domain.support.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PolicyController tests.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class PolicyControllerTest extends BaseControllerTest<Policy> {

    /**
     * All documents.
     */
    private List<Policy> allDocuments = TestData.generatePolicies();

    /**
     * Creates a new instance.
     */
    public PolicyControllerTest() {
        baseUrl = "/policies";
        searchWithPaginationQuery = "?page=1&size=2&sort=name,desc";
        searchWithPaginationAndCriteriaQuery = "?page=0&size=1&sort=description,asc&name=1";
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
     * Negative create tests, invalid rules.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void create400_InvalidRules() throws Exception {
        Policy original = getDocumentForCreateTest();
        String originalJson;
        original.getRules().add(null);
        originalJson = objectMapper.writeValueAsString(original);
        mockMvc //
                .perform( //
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(400));
        original = getDocumentForCreateTest();
        original.getRules().add(original.getRules().get(0));
        originalJson = objectMapper.writeValueAsString(original);
        mockMvc //
                .perform( //
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(400));
        original = getDocumentForCreateTest();
        original.getRules().get(0).setHeader(null);
        originalJson = objectMapper.writeValueAsString(original);
        mockMvc //
                .perform( //
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(400));
        original = getDocumentForCreateTest();
        original.getRules().get(0).getHeader().setHeaderName(original.getRules().get(1).getHeader().getHeaderName());
        originalJson = objectMapper.writeValueAsString(original);
        mockMvc //
                .perform( //
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(400));
        original = getDocumentForCreateTest();
        original.getRules().get(0).getRuleInfo().add(null);
        originalJson = objectMapper.writeValueAsString(original);
        mockMvc //
                .perform( //
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(400));
        original = getDocumentForCreateTest();
        original.getRules().get(0).getRuleInfo().add(original.getRules().get(0).getRuleInfo().get(0));
        originalJson = objectMapper.writeValueAsString(original);
        mockMvc //
                .perform( //
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(400));
    }

    /**
     * Positive create with new headers tests.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void create_NewHeaders() throws Exception {
        Policy original = getDocumentForCreateTest();
        original.getRules().get(0).getHeader().setId(null);
        original.getRules().get(0).getHeader().setHeaderName("new name 1");
        original.getRules().get(1).getHeader().setId(null);
        original.getRules().get(1).getHeader().setHeaderName("new name 2");
        String originalJson = objectMapper.writeValueAsString(original);

        // Create
        mockMvc //
                .perform( //
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().isOk());

        // Check DB
        Policy dbDocument = (Policy) mongoOperations
                .findOne(Query.query(Criteria.where("id").is(original.getId())), original.getClass());
        assertNotNull(dbDocument);
        original.getRules().get(0).getHeader().setId(dbDocument.getRules().get(0).getHeader().getId());
        original.getRules().get(0).getHeader().setCreatedAt(dbDocument.getRules().get(0).getHeader().getCreatedAt());
        original.getRules().get(1).getHeader().setId(dbDocument.getRules().get(1).getHeader().getId());
        original.getRules().get(1).getHeader().setCreatedAt(dbDocument.getRules().get(1).getHeader().getCreatedAt());
        assertEquals(objectMapper.writeValueAsString(original),
                objectMapper.writeValueAsString(dbDocument));

        // New Activity Logs added
        List<ActivityLog> activityLogs = mongoOperations.findAll(ActivityLog.class);
        assertEquals(5, activityLogs.size());

        ActivityLog activityLog;

        // For Headers
        activityLog = activityLogs.get(0);
        assertEquals(Header.class.getSimpleName(), activityLog.getDocumentType());
        assertEquals("admin_user1", activityLog.getOperatedBy());
        assertEquals(OperationType.Create, activityLog.getOperationType());
        assertNotNull(activityLog.getDescription());
        assertNotNull(activityLog.getCreatedAt());

        activityLog = activityLogs.get(1);
        assertEquals(Header.class.getSimpleName(), activityLog.getDocumentType());
        assertEquals("admin_user1", activityLog.getOperatedBy());
        assertEquals(OperationType.Create, activityLog.getOperationType());
        assertNotNull(activityLog.getDescription());
        assertNotNull(activityLog.getCreatedAt());

        activityLog = activityLogs.get(2);
        assertEquals(System.class.getSimpleName(), activityLog.getDocumentType());
        assertEquals("admin_user1", activityLog.getOperatedBy());
        assertEquals(OperationType.Change, activityLog.getOperationType());
        assertNotNull(activityLog.getDescription());
        assertNotNull(activityLog.getCreatedAt());

        activityLog = activityLogs.get(3);
        assertEquals(Module.class.getSimpleName(), activityLog.getDocumentType());
        assertEquals("admin_user1", activityLog.getOperatedBy());
        assertEquals(OperationType.Change, activityLog.getOperationType());
        assertNotNull(activityLog.getDescription());
        assertNotNull(activityLog.getCreatedAt());

        // For Policy
        activityLog = activityLogs.get(4);
        assertEquals(Policy.class.getSimpleName(), activityLog.getDocumentType());
        assertEquals("admin_user1", activityLog.getOperatedBy());
        assertEquals(OperationType.Create, activityLog.getOperationType());
        assertNotNull(activityLog.getDescription());
        assertNotNull(activityLog.getCreatedAt());
    }

    /**
     * Positive update with existing headers tests.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void update_ExistingHeaders() throws Exception {
        Policy original = getDocumentForCreateTest();
        mongoOperations.insert(original);
        original.getRules().get(0).getHeader().setHeaderName("new name 1");
        original.getRules().get(1).getHeader().setHeaderName("new name 2");
        String originalJson = objectMapper.writeValueAsString(original);

        // Create
        mockMvc //
                .perform( //
                        put(baseUrl + "/" + original.getId()).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().isOk());

        // Check DB
        Policy dbDocument = (Policy) mongoOperations
                .findOne(Query.query(Criteria.where("id").is(original.getId())), original.getClass());
        assertNotNull(dbDocument);
        // no updates
        assertEquals(original.getRules().get(0).getHeader().getId(), dbDocument.getRules().get(0).getHeader().getId());
        original.getRules().get(0).getHeader().setId(dbDocument.getRules().get(0).getHeader().getId());
        original.getRules().get(0).getHeader().setCreatedAt(dbDocument.getRules().get(0).getHeader().getCreatedAt());
        original.getRules().get(1).getHeader().setId(dbDocument.getRules().get(1).getHeader().getId());
        original.getRules().get(1).getHeader().setCreatedAt(dbDocument.getRules().get(1).getHeader().getCreatedAt());
        assertEquals(objectMapper.writeValueAsString(original),
                objectMapper.writeValueAsString(dbDocument));

        // New Activity Logs added
        List<ActivityLog> activityLogs = mongoOperations.findAll(ActivityLog.class);
        assertEquals(5, activityLogs.size());

        ActivityLog activityLog;

        // For Headers
        activityLog = activityLogs.get(0);
        assertEquals(Header.class.getSimpleName(), activityLog.getDocumentType());
        assertEquals("admin_user1", activityLog.getOperatedBy());
        assertEquals(OperationType.Change, activityLog.getOperationType());
        assertNotNull(activityLog.getDescription());
        assertNotNull(activityLog.getCreatedAt());

        activityLog = activityLogs.get(1);
        assertEquals(Header.class.getSimpleName(), activityLog.getDocumentType());
        assertEquals("admin_user1", activityLog.getOperatedBy());
        assertEquals(OperationType.Change, activityLog.getOperationType());
        assertNotNull(activityLog.getDescription());
        assertNotNull(activityLog.getCreatedAt());

        activityLog = activityLogs.get(2);
        assertEquals(System.class.getSimpleName(), activityLog.getDocumentType());
        assertEquals("admin_user1", activityLog.getOperatedBy());
        assertEquals(OperationType.Change, activityLog.getOperationType());
        assertNotNull(activityLog.getDescription());
        assertNotNull(activityLog.getCreatedAt());

        activityLog = activityLogs.get(3);
        assertEquals(Module.class.getSimpleName(), activityLog.getDocumentType());
        assertEquals("admin_user1", activityLog.getOperatedBy());
        assertEquals(OperationType.Change, activityLog.getOperationType());
        assertNotNull(activityLog.getDescription());
        assertNotNull(activityLog.getCreatedAt());

        // For Policy
        activityLog = activityLogs.get(4);
        assertEquals(Policy.class.getSimpleName(), activityLog.getDocumentType());
        assertEquals("admin_user1", activityLog.getOperatedBy());
        assertEquals(OperationType.Change, activityLog.getOperationType());
        assertNotNull(activityLog.getDescription());
        assertNotNull(activityLog.getCreatedAt());
    }

    /**
     * Negative create with invalid headers tests.
     *
     * @throws Exception if any error occurs
     */
    public void create400_InvalidHeaders() throws Exception {
        Policy original = getDocumentForCreateTest();
        String originalJson;

        original.getRules().get(0).setHeader(null);
        originalJson = objectMapper.writeValueAsString(original);
        mockMvc //
                .perform( //
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(400));

        original.getRules().get(0).setHeader(original.getRules().get(1).getHeader());
        originalJson = objectMapper.writeValueAsString(original);
        mockMvc //
                .perform( //
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(400));

        original.getRules().remove(0);
        original.getRules().get(0).getHeader().setId(null);
        originalJson = objectMapper.writeValueAsString(original);
        mockMvc //
                .perform( //
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(400));

        original.getRules().get(0).getHeader().setId("123456");
        originalJson = objectMapper.writeValueAsString(original);
        mockMvc //
                .perform( //
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(400));
    }

    /**
     * Positive create with null rule name.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void create_nullNameRulesHeaders() throws Exception {
        Policy original = getDocumentForCreateTest();
        // two null name rules
        original.getRules().get(0).setName(null);
        original.getRules().get(1).setName(null);
        String originalJson = objectMapper.writeValueAsString(original);
        mockMvc //
                .perform( //
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().isOk()).andExpect(content().json(originalJson, true));
    }

    /**
     * Positive create with new module tests.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void create_NewModule() throws Exception {
        Policy original = getDocumentForCreateTest();
        original.getModule().setId(null);
        original.getModule().setName("new name");
        String originalJson = objectMapper.writeValueAsString(original);

        // Create
        mockMvc //
                .perform( //
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().isOk());

        // Check DB
        Policy dbDocument = (Policy) mongoOperations
                .findOne(Query.query(Criteria.where("id").is(original.getId())), original.getClass());
        assertNotNull(dbDocument);
        original.getModule().setId(dbDocument.getModule().getId());
        original.getModule().setCreatedAt(dbDocument.getModule().getCreatedAt());
        assertEquals(objectMapper.writeValueAsString(original),
                objectMapper.writeValueAsString(dbDocument));

        // New Activity Logs added
        List<ActivityLog> activityLogs = mongoOperations.findAll(ActivityLog.class);
        assertEquals(5, activityLogs.size());

        // For Headers
        ActivityLog activityLog = activityLogs.get(0);
        assertEquals(Header.class.getSimpleName(), activityLog.getDocumentType());
        assertEquals("admin_user1", activityLog.getOperatedBy());
        assertEquals(OperationType.Change, activityLog.getOperationType());
        assertNotNull(activityLog.getDescription());
        assertNotNull(activityLog.getCreatedAt());

        activityLog = activityLogs.get(1);
        assertEquals(Header.class.getSimpleName(), activityLog.getDocumentType());
        assertEquals("admin_user1", activityLog.getOperatedBy());
        assertEquals(OperationType.Change, activityLog.getOperationType());
        assertNotNull(activityLog.getDescription());
        assertNotNull(activityLog.getCreatedAt());

        activityLog = activityLogs.get(2);
        assertEquals(System.class.getSimpleName(), activityLog.getDocumentType());
        assertEquals("admin_user1", activityLog.getOperatedBy());
        assertEquals(OperationType.Change, activityLog.getOperationType());
        assertNotNull(activityLog.getDescription());
        assertNotNull(activityLog.getCreatedAt());

        activityLog = activityLogs.get(3);
        assertEquals(Module.class.getSimpleName(), activityLog.getDocumentType());
        assertEquals("admin_user1", activityLog.getOperatedBy());
        assertEquals(OperationType.Create, activityLog.getOperationType());
        assertNotNull(activityLog.getDescription());
        assertNotNull(activityLog.getCreatedAt());

        // For Policy
        activityLog = activityLogs.get(4);
        assertEquals(Policy.class.getSimpleName(), activityLog.getDocumentType());
        assertEquals("admin_user1", activityLog.getOperatedBy());
        assertEquals(OperationType.Create, activityLog.getOperationType());
        assertNotNull(activityLog.getDescription());
        assertNotNull(activityLog.getCreatedAt());
    }

    /**
     * Negative update with existing module tests.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void update_ExistingModule() throws Exception {
        Policy original = getDocumentForCreateTest();
        mongoOperations.insert(original);
        original.getModule().setName("new name");
        String originalJson = objectMapper.writeValueAsString(original);

        // Create
        mockMvc //
                .perform( //
                        put(baseUrl + "/" + original.getId()).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().isOk());

        // Check DB
        Policy dbDocument = (Policy) mongoOperations
                .findOne(Query.query(Criteria.where("id").is(original.getId())), original.getClass());
        assertNotNull(dbDocument);
        original.getModule().setId(dbDocument.getModule().getId());
        original.getModule().setCreatedAt(dbDocument.getModule().getCreatedAt());
        assertEquals(objectMapper.writeValueAsString(original),
                objectMapper.writeValueAsString(dbDocument));

        // New Activity Logs added
        List<ActivityLog> activityLogs = mongoOperations.findAll(ActivityLog.class);
        assertEquals(5, activityLogs.size());

        // For Headers
        ActivityLog activityLog = activityLogs.get(0);
        assertEquals(Header.class.getSimpleName(), activityLog.getDocumentType());
        assertEquals("admin_user1", activityLog.getOperatedBy());
        assertEquals(OperationType.Change, activityLog.getOperationType());
        assertNotNull(activityLog.getDescription());
        assertNotNull(activityLog.getCreatedAt());

        activityLog = activityLogs.get(1);
        assertEquals(Header.class.getSimpleName(), activityLog.getDocumentType());
        assertEquals("admin_user1", activityLog.getOperatedBy());
        assertEquals(OperationType.Change, activityLog.getOperationType());
        assertNotNull(activityLog.getDescription());
        assertNotNull(activityLog.getCreatedAt());

        activityLog = activityLogs.get(2);
        assertEquals(System.class.getSimpleName(), activityLog.getDocumentType());
        assertEquals("admin_user1", activityLog.getOperatedBy());
        assertEquals(OperationType.Change, activityLog.getOperationType());
        assertNotNull(activityLog.getDescription());
        assertNotNull(activityLog.getCreatedAt());

        activityLog = activityLogs.get(3);
        assertEquals(Module.class.getSimpleName(), activityLog.getDocumentType());
        assertEquals("admin_user1", activityLog.getOperatedBy());
        assertEquals(OperationType.Change, activityLog.getOperationType());
        assertNotNull(activityLog.getDescription());
        assertNotNull(activityLog.getCreatedAt());

        // For Policy
        activityLog = activityLogs.get(4);
        assertEquals(Policy.class.getSimpleName(), activityLog.getDocumentType());
        assertEquals("admin_user1", activityLog.getOperatedBy());
        assertEquals(OperationType.Change, activityLog.getOperationType());
        assertNotNull(activityLog.getDescription());
        assertNotNull(activityLog.getCreatedAt());
    }

    /**
     * Negative create with invalid module tests.
     *
     * @throws Exception if any error occurs
     */
    public void create400_InvalidModule() throws Exception {
        Policy original = getDocumentForCreateTest();

        original.getModule().setId("12340");
        String originalJson = objectMapper.writeValueAsString(original);
        mockMvc //
                .perform( //
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(400));

        original.setModule(null);
        originalJson = objectMapper.writeValueAsString(original);
        mockMvc //
                .perform( //
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(400));
    }

    /**
     * Positive search by module id tests.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void searchByModuleId() throws Exception {
        Module module = TestData.generateModules().get(0);

        List<Policy> policies = allDocuments.stream()
                .filter(document -> document.getModule().getId().equals(module.getId()))
                .collect(Collectors.toList());
        SearchResult<Policy> result = new SearchResult<>(policies.size(), policies);
        String expectedJson = objectMapper.writeValueAsString(result);

        mockMvc //
                .perform( //
                        get(baseUrl + "?moduleId=" + module.getId()) //
                                .with(user("ro_user1").password("secret").roles(Role.RO.toString()))) //
                .andExpect(status().isOk()) //
                .andExpect(content().json(expectedJson, true));
    }

    /**
     * Positive delete by ids tests.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void deleteByIds() throws Exception {
        List<String> ids =
                allDocuments.subList(0, 3).stream().map(d -> d.getId()).collect(Collectors.toList());

        mockMvc //
                .perform( //
                        delete(baseUrl + "?ids=" + StringUtils.collectionToCommaDelimitedString(ids)) //
                                .with(user("rw_user1").password("secret").roles(Role.RW.toString()))) //
                .andExpect(status().isOk());

        // Check DB
        assertFalse(
                mongoOperations.exists(Query.query(Criteria.where("id").is(ids.get(0))), Policy.class));
        assertFalse(
                mongoOperations.exists(Query.query(Criteria.where("id").is(ids.get(1))), Policy.class));
        assertFalse(
                mongoOperations.exists(Query.query(Criteria.where("id").is(ids.get(2))), Policy.class));
    }

    /**
     * Negative delete by ids tests, not found.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void deleteByIds404() throws Exception {
        mockMvc //
                .perform( //
                        delete(baseUrl + "?ids=11111") //
                                .with(user("rw_user1").password("secret").roles(Role.RW.toString()))) //
                .andExpect(status().is(404));
    }

    /**
     * Gets expected result for search tests.
     *
     * @return the expected result
     */
    @Override
    protected SearchResult<Policy> getExpectedResultForSearchTests() {
        return new SearchResult<>(allDocuments.size(), allDocuments);
    }

    /**
     * Gets expected result for search with pagination tests.
     *
     * @return the expected result
     */
    @Override
    protected SearchResult<Policy> getExpectedResultForSearchWithPaginationTests() {
        List<Policy> documents = new ArrayList<>();
        documents.add(allDocuments.get(6));
        documents.add(allDocuments.get(5));

        return new SearchResult<>(allDocuments.size(), documents);
    }

    /**
     * Gets expected result for search with pagination and criteria tests.
     *
     * @return the expected result
     */
    @Override
    protected SearchResult<Policy> getExpectedResultForSearchWithPaginationAndCriteriaTest() {
        List<Policy> documents = allDocuments.subList(0, 1);

        return new SearchResult<>(2, documents);
    }

    /**
     * Gets document for create tests.
     *
     * @return the document
     */
    @Override
    protected Policy getDocumentForCreateTest() {
        return TestData.createNewPolicy();
    }

    /**
     * Gets document for duplicated tests.
     *
     * @return the document
     */
    @Override
    protected Policy getDocumentForDuplicatedTest() {
        Policy document = new Policy();
        document.setName(allDocuments.get(0).getName());
        document.setModule(allDocuments.get(0).getModule());
        return document;
    }

    /**
     * Gets document for update tests.
     *
     * @return the document
     */
    @Override
    protected Policy getDocumentForUpdateTest() {
        Policy document = TestData.generatePolicies().get(1);

        document.setDescription("new description");
        document.setName("new name");
        document.setEnabled(!document.getEnabled());
        document.setModule(allDocuments.get(0).getModule());
        document.setRules(allDocuments.get(0).getRules());
        return document;
    }

    /**
     * Gets document for delete tests.
     *
     * @return the document
     */
    @Override
    protected Policy getDocumentForGetDeleteTest() {
        return allDocuments.get(1);
    }
}
