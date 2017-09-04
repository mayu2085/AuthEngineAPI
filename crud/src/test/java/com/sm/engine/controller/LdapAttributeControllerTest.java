package com.sm.engine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sm.engine.TestApplication;
import com.sm.engine.TestData;
import com.sm.engine.domain.LdapAttribute;
import com.sm.engine.domain.Role;
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

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * LdapAttributeController tests.
 */
@ContextConfiguration(classes = {TestApplication.class})
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class LdapAttributeControllerTest {

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
    private String baseUrl = "/ldap-attributes";

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

    private void removeTestData() {
        mongoOperations.remove(new Query(), LdapAttribute.class);
    }

    private void insertTestData() {
        mongoOperations.insert(TestData.generateLdapAttributes(), LdapAttribute.class);
    }

    /**
     * 401 Unauthorized tests.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void status401() throws Exception {
        mockMvc.perform(get(baseUrl)).andExpect(status().is(401));
    }

    /**
     * Positive get all names tests.
     *
     * @throws Exception if any error occurs
     */
    @Test
    public void getAllNames() throws Exception {
        List<LdapAttribute> allDocuments = TestData.generateLdapAttributes().subList(0, 4);
        List<String> names = allDocuments.stream().map(d -> d.getName()).collect(Collectors.toList());
        String expectedJson = objectMapper.writeValueAsString(names);

        mockMvc
                .perform( //
                        get(baseUrl) //
                                .with(user("ro_user1").password("secret").roles(Role.RO.toString()))) //
                .andExpect(status().isOk()) //
                .andExpect(content().json(expectedJson, true));
        mockMvc
                .perform( //
                        get(baseUrl) //
                                .with(user("rw_user1").password("secret").roles(Role.RW.toString()))) //
                .andExpect(status().isOk()) //
                .andExpect(content().json(expectedJson, true));
        mockMvc
                .perform( //
                        get(baseUrl) //
                                .with(user("admin_user1").password("secret").roles(Role.Admin.toString()))) //
                .andExpect(status().isOk()) //
                .andExpect(content().json(expectedJson, true));
    }
}
