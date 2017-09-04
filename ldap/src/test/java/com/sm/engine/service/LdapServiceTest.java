package com.sm.engine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sm.engine.config.LdapConfig;
import com.sm.engine.domain.UserAttributesRequest;
import com.sm.engine.service.Impl.LdapServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.query.SearchScope;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


/**
 * The tests for LdapService.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = { "com.sm.engine" })
@TestPropertySource({ "classpath:application-test.properties" })
public class LdapServiceTest {
    /**
     * The ldap service used in test.
     */
    private LdapService service;

    /**
     * The json tester for list of string.
     */
    private JacksonTester<List<String>> listJacksonTester;

    /**
     * The json tester for map.
     */
    private JacksonTester<Map<String,Object>> mapJacksonTester;

    /**
     * The ldap url.
     */
    @Value("${ldap.url}")
    private String url;

    /**
     * The ldap root.
     */
    @Value("${ldap.root}")
    private String root;

    /**
     * The ldap user dn.
     */
    @Value("${ldap.userDn}")
    private String userDn;

    /**
     * The ldap password.
     */
    @Value("${ldap.password}")
    private String password;

    /**
     * The ldap user search base.
     */
    @Value("${ldap.userSearchBase}")
    private String userSearchBase;

    /**
     * The ldap user attribute.
     */
    @Value("${ldap.userAttribute}")
    private String userAttribute;

    /**
     * Setup test.
     */
    @Before
    public void setup() {
        if(service == null){
            LdapConfig config = new LdapConfig();
            config.setUrl(url);
            config.setRoot(root);
            config.setUserDn(userDn);
            config.setPassword(password);
            config.setUserSearchBase(userSearchBase);
            config.setUserAttribute(userAttribute);
            config.setSearchScope(SearchScope.SUBTREE);
            service = new LdapServiceImpl(config);
        }
        ObjectMapper objectMapper = new ObjectMapper();
       JacksonTester.initFields(this, objectMapper);
    }

    /**
     * Test authenticate with valid user name/password.
     *
     * @throws Exception throws if any error happens
     */
    @Test
    public void authenticateWithValidUser() throws Exception {
        assertTrue(service.authenticate("ben", "benspassword"));
    }

    /**
     * Test authenticate with invalid user name/password.
     *
     * @throws Exception throws if any error happens
     */
    @Test
    public void authenticateWithInvalidUsernameOrPassword() throws Exception {
        assertFalse(service.authenticate("ben", "wrong"));
        assertFalse(service.authenticate("wrong", "benspassword"));
    }

    /**
     * Test find user dn method.
     *
     * @throws Exception throws if any error happens
     */
    @Test
    public void findUserDN() throws Exception {
        assertEquals("uid=ben,ou=people",
                service.findUserDN("ben"));
        assertNull(service.findUserDN("notexist"));
    }


    /**
     * Test list users method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void listUsers() throws Exception {
        assertThat(listJacksonTester.write(service.listUsers()))
                .isEqualToJson("users.json");
    }

    /**
     * Test evaluate rule method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void evaluateRule() throws Exception {
        assertFalse(service.evaluateRule("cn = CS Agent;ou = PartnerWEB"));
        assertTrue(service.evaluateRule("uid=joe,ou=otherpeople"));
    }

    /**
     * Test get user attributes method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void getUserAttributes() throws Exception {
        UserAttributesRequest request = new UserAttributesRequest();
        request.setUserDN("uid=ben,ou=people");
        request.setAttributes(new String[]{"sn","cn","uid","objectclass"});
        assertThat(mapJacksonTester.write(service.getUserAttributes(request)))
                .isEqualToJson("userAttributesResult.json");
    }
}
