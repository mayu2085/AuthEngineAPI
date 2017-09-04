package com.sm.engine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sm.engine.TestApplication;
import com.sm.engine.TestData;
import com.sm.engine.config.JwtConfig;
import com.sm.engine.config.TestLdapConfig;
import com.sm.engine.domain.LdapConfiguration;
import com.sm.engine.domain.LoginRequest;
import com.sm.engine.domain.LoginResponse;
import com.sm.engine.domain.Role;
import com.sm.engine.domain.User;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * LoginController tests.
 */
@ContextConfiguration(classes = {TestApplication.class})
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class LoginControllerTest {

    /**
     * The base url to test login method.
     */
    private static final String BASE_URL = "/login";

    /**
     * The base url to test jwt token.
     */
    private static final String TOKEN_BASE = "/users";

    /**
     * The RO role jwt token..
     */
    @Value("${jwt.roRoleToken}")
    private String roRoleToken;

    /**
     * The not found user token..
     */
    @Value("${jwt.notFoundUserToken}")
    private String notFoundUserToken;

    /**
     * The disabled user token..
     */
    @Value("${jwt.disabledUserToken}")
    private String disabledUserToken;

    /**
     * The expire token..
     */
    @Value("${jwt.expireToken}")
    private String expireToken;

    /**
     * The web application context.
     */
    @Autowired
    private WebApplicationContext context;

    /**
     * The mongo operations.
     */
    @Autowired
    private MongoOperations mongoOperations;

    /**
     * The mock mvc.
     */
    private MockMvc mockMvc;

    /**
     * The object mapper.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * The injected jwt config
     */
    @Autowired
    private JwtConfig jwtConfig;

    /**
     * The test ldap configurations.
     */
    @Autowired
    private TestLdapConfig testLdapConfig;

    /**
     * Prepare test.
     */
    @Before
    public void before() {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        }
        mongoOperations.remove(new Query(), User.class);
        mongoOperations.remove(new Query(), LdapConfiguration.class);
        mongoOperations.insert(TestData.generateUsers(), User.class);
    }

    /**
     * Test login method without valid ldap configuration.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void loginWithoutLdapConfiguration() throws Exception {
        LoginRequest request = getAdminLogin();
        mockMvc
                .perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    /**
     * Test login method with right username/password.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void login() throws Exception {
        setupLdapConfiguration();
        LoginRequest request = getAdminLogin();
        mockMvc
                .perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.username").value("admin_user1"))
                .andExpect(jsonPath("$.user.role").value(Role.Admin.toString()));
    }

    /**
     * Test login method with wrong username or password.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void loginWithWrongUsernameOrPassword() throws Exception {
        setupLdapConfiguration();
        LoginRequest request = getAdminLogin();
        request.setUsername("wrongusername");
        mockMvc
                .perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
        request = getAdminLogin();
        request.setPassword("wrongusername");
        mockMvc
                .perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test login method with not found user in mongo.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void loginWithNotFoundUserInMongo() throws Exception {
        setupLdapConfiguration();
        LoginRequest request = new LoginRequest();
        request.setUsername("ben");
        request.setPassword("benspassword");
        mockMvc
                .perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }


    /**
     * Test login method with disabled user in mongo.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void loginWithDisabledUserInMongo() throws Exception {
        setupLdapConfiguration();
        LoginRequest request = new LoginRequest();
        request.setUsername("ro_user2");
        request.setPassword("secret");
        mockMvc
                .perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }


    /**
     * Test jwt token from login method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void testJWTToken() throws Exception {
        setupLdapConfiguration();
        LoginRequest request = getAdminLogin();
        String res = mockMvc
                .perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        LoginResponse result = objectMapper.readValue(res, LoginResponse.class);
        String token = result.getToken();

        mockMvc
                .perform(
                        get(TOKEN_BASE))
                .andExpect(status().isUnauthorized());
        mockMvc
                .perform(
                        get(TOKEN_BASE).accept(MediaType.APPLICATION_JSON)
                                .header(jwtConfig.getAuthHeader(),
                                        jwtConfig.getTokenPrefix() + " " + token))
                .andExpect(status().isOk());
        mockMvc
                .perform(
                        get(TOKEN_BASE).accept(MediaType.APPLICATION_JSON)
                                .header(jwtConfig.getAuthHeader(), "wrongprefix " + token))
                .andExpect(status().isUnauthorized());
        mockMvc
                .perform(
                        get(TOKEN_BASE).accept(MediaType.APPLICATION_JSON)
                                .header("wrongname",
                                        jwtConfig.getTokenPrefix() + " " + token))
                .andExpect(status().isUnauthorized());
        mockMvc
                .perform(
                        get(TOKEN_BASE).accept(MediaType.APPLICATION_JSON)
                                .header(jwtConfig.getAuthHeader(),
                                        jwtConfig.getTokenPrefix() + " " + roRoleToken))
                .andExpect(status().isForbidden());
    }

    /**
     * Test jwt token with not found user.
     *
     * @throws Exception throws if any error happens.
     */
    @Test(expected = UsernameNotFoundException.class)
    public void testNotFoundUserJWTToken() throws Exception {
        mockMvc
                .perform(
                        get(TOKEN_BASE).accept(MediaType.APPLICATION_JSON)
                                .header(jwtConfig.getAuthHeader(),
                                        jwtConfig.getTokenPrefix() + " " + notFoundUserToken));
    }

    /**
     * Test expired jwt token.
     *
     * @throws Exception throws if any error happens.
     */
    @Test(expected = ExpiredJwtException.class)
    public void testExpiredJWTToken() throws Exception {
        mockMvc
                .perform(
                        get(TOKEN_BASE).accept(MediaType.APPLICATION_JSON)
                                .header(jwtConfig.getAuthHeader(),
                                        jwtConfig.getTokenPrefix() + " " + expireToken));
    }

    /**
     * Test jwt token with disabled user.
     *
     * @throws Exception throws if any error happens.
     */
    @Test(expected = DisabledException.class)
    public void testDisabledUserJWTToken() throws Exception {
        mockMvc
                .perform(
                        get(TOKEN_BASE).accept(MediaType.APPLICATION_JSON)
                                .header(jwtConfig.getAuthHeader(),
                                        jwtConfig.getTokenPrefix() + " " + disabledUserToken));
    }

    /**
     * Generate admin role user login request.
     *
     * @return the admin role user login request.
     */
    private LoginRequest getAdminLogin() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin_user1");
        request.setPassword("secret");
        return request;
    }

    /**
     * Setup valid ldap configuration.
     */
    private void setupLdapConfiguration() {
        mongoOperations.insert(TestData.createValidLdapConfiguration(testLdapConfig));
    }
}
