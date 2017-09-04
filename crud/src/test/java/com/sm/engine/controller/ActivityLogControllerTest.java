package com.sm.engine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sm.engine.TestApplication;
import com.sm.engine.TestData;
import com.sm.engine.domain.ActivityLog;
import com.sm.engine.domain.OperationType;
import com.sm.engine.domain.Role;
import com.sm.engine.domain.support.SearchResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ActivityLogController tests.
 */
@ContextConfiguration(classes = {TestApplication.class})
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ActivityLogControllerTest {

    /**
     * The web application context.
     */
    @Autowired
    private WebApplicationContext context;

    /**
     * The Mongo operations instance.
     */
    @Autowired
    private MongoOperations mongoOperations;

    /**
     * The mock MVC.
     */
    private MockMvc mockMvc;

    /**
     * The base url.
     */
    private String baseUrl = "/activity-logs";

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
     * Removes test data.
     */
    private void removeTestData() {
        mongoOperations.remove(new Query(), ActivityLog.class);
    }

    /**
     * Inserts test data.
     */
    private void insertTestData() {
        mongoOperations.insert(TestData.generateActivityLogs(), ActivityLog.class);
    }

    /**
     * 401 - Unauthorized tests.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void status401() throws Exception {
        mockMvc.perform(get(baseUrl)).andExpect(status().is(401));
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
    }

    /**
     * Positive search test.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void search() throws Exception {
        List<ActivityLog> allDocuments = TestData.generateActivityLogs();

        // Default
        SearchResult<ActivityLog> result = new SearchResult<>(allDocuments.size(), allDocuments);
        String expectedJson = objectMapper.writeValueAsString(result);
        mockMvc
                .perform( //
                        get(baseUrl) //
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString()))) //
                .andExpect(status().isOk()) //
                .andExpect(content().json(expectedJson, true));

        // Pagination
        List<ActivityLog> documents = allDocuments.stream().sorted(new Comparator<ActivityLog>() {
            @Override
            public int compare(ActivityLog o1, ActivityLog o2) {
                return o1.getDocumentType().compareTo(o2.getDocumentType());
            }
        }).collect(Collectors.toList()).subList(3, 6);
        result = new SearchResult<>(allDocuments.size(), documents);
        expectedJson = objectMapper.writeValueAsString(result);
        mockMvc
                .perform( //
                        get(baseUrl + "?page=1&size=3&sort=documentType,asc") //
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString()))) //
                .andExpect(status().isOk()) //
                .andExpect(content().json(expectedJson, true));

        // Criteria
        documents = allDocuments.stream().sorted(new Comparator<ActivityLog>() {
            @Override
            public int compare(ActivityLog o1, ActivityLog o2) {
                return o2.getCreatedAt().compareTo(o1.getCreatedAt());
            }
        }).filter(d -> {
            return d.getOperationType() == OperationType.Create && d.getDocumentType().contains("U")
                    && d.getOperatedBy().contains("n") && d.getDescription().contains("d");
        }).collect(Collectors.toList()).subList(0, 3);
        result = new SearchResult<>(3, documents);
        expectedJson = objectMapper.writeValueAsString(result);
        mockMvc
                .perform( //
                        get(baseUrl + //
                                "?page=0&size=3&sort=createdAt,desc" + //
                                "&operationType=" + OperationType.Create + //
                                "&documentType=U&operatedBy=n&description=d")
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString()))) //
                .andExpect(status().isOk()) //
                .andExpect(content().json(expectedJson, true));
    }

    /**
     * Negative search test.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void search400() throws Exception {
        mockMvc
                .perform( //
                        get(baseUrl + "?operationType=invalid") //
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString()))) //
                .andExpect(status().is(400));
    }
}
