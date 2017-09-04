package com.sm.engine.controller;

import com.sm.engine.TestData;
import com.sm.engine.domain.ActivityLog;
import com.sm.engine.domain.Module;
import com.sm.engine.domain.OperationType;
import com.sm.engine.domain.Role;
import com.sm.engine.domain.support.SearchResult;
import com.sm.engine.service.PolicyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ModuleController tests.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ModuleControllerTest extends BaseControllerTest<Module> {
    /**
     * All documents.
     */
    private List<Module> allDocuments = TestData.generateModules();

    /**
     * Creates a new instance.
     */
    public ModuleControllerTest() {
        baseUrl = "/modules";
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
     * Positive search by system id tests.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void searchWithSystemId() throws Exception {
        List<Module> documents = new ArrayList<>();
        documents.add(allDocuments.get(0));
        documents.add(allDocuments.get(2));

        SearchResult<Module> expected = new SearchResult<>(2, documents);

        String expectedJson = objectMapper.writeValueAsString(expected);

        mockMvc //
                .perform( //
                        get(baseUrl + "?systemId=" + allDocuments.get(0).getSystem().getId()) //
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString()))) //
                .andExpect(status().isOk()) //
                .andExpect(content().json(expectedJson, true));
    }

    /**
     * Positive create with a nested new system tests.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void create_NewSystem() throws Exception {
        Module original = TestData.createNewModule();

        original.getSystem().setId(null);
        original.getSystem().setName("new system name");
        String originalJson = objectMapper.writeValueAsString(original);

        mockMvc //
                .perform( //
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().isOk());

        // Check DB
        Module dbDocument = mongoOperations
                .findOne(Query.query(Criteria.where("id").is(original.getId())), original.getClass());
        assertNotNull(dbDocument);
        original.getSystem().setId(dbDocument.getSystem().getId());
        original.getSystem().setCreatedAt(dbDocument.getSystem().getCreatedAt());
        assertEquals(objectMapper.writeValueAsString(original), objectMapper.writeValueAsString(dbDocument));

        // New Activity Logs added
        List<ActivityLog> activityLogs = mongoOperations.findAll(ActivityLog.class);
        assertTrue(activityLogs.size() == 2);

        // For System
        assertEquals(dbDocument.getSystem().getClass().getSimpleName(),
                activityLogs.get(0).getDocumentType());
        assertEquals("admin_user1", activityLogs.get(0).getOperatedBy());
        assertEquals(OperationType.Create, activityLogs.get(0).getOperationType());
        assertNotNull(activityLogs.get(0).getDescription());
        assertNotNull(activityLogs.get(0).getCreatedAt());

        // For Module
        assertEquals(dbDocument.getClass().getSimpleName(), activityLogs.get(1).getDocumentType());
        assertEquals("admin_user1", activityLogs.get(1).getOperatedBy());
        assertEquals(OperationType.Create, activityLogs.get(1).getOperationType());
        assertNotNull(activityLogs.get(0).getDescription());
        assertNotNull(activityLogs.get(0).getCreatedAt());
    }

    /**
     * Positive update with existing system tests.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void update_ExistingSystem() throws Exception {
        Module original = TestData.generateModules().get(0);
        original.getSystem().setDescription("new description");
        String originalJson = objectMapper.writeValueAsString(original);

        mockMvc //
                .perform( //
                        put(baseUrl + "/" + original.getId()).contentType(MediaType.APPLICATION_JSON).content(originalJson)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().isOk());

        // Check DB
        Module dbDocument = (Module) mongoOperations
                .findOne(Query.query(Criteria.where("id").is(original.getId())), original.getClass());
        assertNotNull(dbDocument);
        original.getSystem().setId(dbDocument.getSystem().getId());
        original.getSystem().setCreatedAt(dbDocument.getSystem().getCreatedAt());
        // system id not change so update only
        assertEquals(original.getSystem().getId(), dbDocument.getSystem().getId());
        assertEquals(objectMapper.writeValueAsString(original), objectMapper.writeValueAsString(dbDocument));


        // New Activity Logs added
        List<ActivityLog> activityLogs = mongoOperations.findAll(ActivityLog.class);
        assertTrue(activityLogs.size() == 2);

        // For System
        assertEquals(dbDocument.getSystem().getClass().getSimpleName(),
                activityLogs.get(0).getDocumentType());
        assertEquals("admin_user1", activityLogs.get(0).getOperatedBy());
        assertEquals(OperationType.Change, activityLogs.get(0).getOperationType());
        assertNotNull(activityLogs.get(0).getDescription());
        assertNotNull(activityLogs.get(0).getCreatedAt());

        // For Module
        assertEquals(dbDocument.getClass().getSimpleName(), activityLogs.get(1).getDocumentType());
        assertEquals("admin_user1", activityLogs.get(1).getOperatedBy());
        assertEquals(OperationType.Change, activityLogs.get(1).getOperationType());
        assertNotNull(activityLogs.get(0).getDescription());
        assertNotNull(activityLogs.get(0).getCreatedAt());
    }

    /**
     * Negative create with invalid system test.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void create_InvalidSystem() throws Exception {
        Module module = TestData.createNewModule();

        // Not exist
        module.getSystem().setId("577e8a907100000000000000");
        String json = objectMapper.writeValueAsString(module);

        mockMvc //
                .perform( //
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(json)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(404));

        // Id is null, name duplicates
        module.getSystem().setId(null);
        json = objectMapper.writeValueAsString(module);

        mockMvc //
                .perform( //
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(json)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(400));

        // Name is null
        module.getSystem().setName(null);
        json = objectMapper.writeValueAsString(module);

        mockMvc //
                .perform( //
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(json)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(400));
        // name is valid
        module.getSystem().setName("valid system test name");
        json = objectMapper.writeValueAsString(module);
      mockMvc //
                .perform( //
                        post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(json)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
              .andExpect(status().isOk());
    }

    /**
     * Negative update with invalid system test.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void update_InvalidSystem() throws Exception {
        Module module = getDocumentForUpdateTest();

        // Not exist
        module.getSystem().setId("577e8a907100000000000000");
        String json = objectMapper.writeValueAsString(module);

        mockMvc //
                .perform( //
                        put(baseUrl + "/" + module.getId()).contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(404));

        // Id is null
        module.getSystem().setId(null);
        json = objectMapper.writeValueAsString(module);

        mockMvc //
                .perform( //
                        put(baseUrl + "/" + module.getId()).contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString())))
                .andExpect(status().is(400));
    }

    /**
     * Gets expected result for search tests.
     *
     * @return the expected result
     */
    @Override
    protected SearchResult<Module> getExpectedResultForSearchTests() {
        return new SearchResult<>(allDocuments.size(), allDocuments);
    }

    /**
     * Gets expected result for search with pagination tests.
     *
     * @return the expected result
     */
    @Override
    protected SearchResult<Module> getExpectedResultForSearchWithPaginationTests() {
        List<Module> documents = new ArrayList<>();
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
    protected SearchResult<Module> getExpectedResultForSearchWithPaginationAndCriteriaTest() {
        List<Module> documents = allDocuments.subList(0, 1);

        return new SearchResult<>(1, documents);
    }

    /**
     * Gets document for create tests.
     *
     * @return the document
     */
    @Override
    protected Module getDocumentForCreateTest() {
        return TestData.createNewModule();
    }

    /**
     * Gets document for duplicated tests.
     *
     * @return the document
     */
    @Override
    protected Module getDocumentForDuplicatedTest() {
        Module document = new Module();
        document.setName(allDocuments.get(0).getName());
        document.setSystem(allDocuments.get(0).getSystem());
        return document;
    }

    /**
     * Get document for update tests.
     *
     * @return the document
     */
    @Override
    protected Module getDocumentForUpdateTest() {
        Module document = TestData.generateModules().get(1);

        document.setDescription("new description");
        document.setName("new name");
        document.setSystem(allDocuments.get(0).getSystem());

        return document;
    }

    /**
     * Gets document for delete tests.
     *
     * @return the document
     */
    @Override
    protected Module getDocumentForGetDeleteTest() {
        return allDocuments.get(1);
    }
}
