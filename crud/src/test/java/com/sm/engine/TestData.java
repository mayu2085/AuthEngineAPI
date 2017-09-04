package com.sm.engine;

import com.sm.engine.config.TestLdapConfig;
import com.sm.engine.domain.ActivityLog;
import com.sm.engine.domain.Header;
import com.sm.engine.domain.HeaderType;
import com.sm.engine.domain.LdapAttribute;
import com.sm.engine.domain.LdapAttributeNameValue;
import com.sm.engine.domain.LdapConfiguration;
import com.sm.engine.domain.Module;
import com.sm.engine.domain.OperationType;
import com.sm.engine.domain.Policy;
import com.sm.engine.domain.Role;
import com.sm.engine.domain.Rule;
import com.sm.engine.domain.System;
import com.sm.engine.domain.User;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Generates the test data used by tests.
 */
public class TestData {
    /**
     * The ldap attributes.
     */
    private static final String[] LDAP_ATTRS = new String[]{"sn", "uid", "objectclass"};

    /**
     * The activity log base id.
     */
    private static final String ACTIVITY_LOG_BASE_ID = "577e8a90100000000000000";

    /**
     * The header base id.
     */
    private static final String HEADER_BASE_ID = "577e8a90200000000000000";

    /**
     * The ldap attribute base id.
     */
    private static final String LDAP_ATTRIBUTE_BASE_ID = "577e8a90300000000000000";

    /**
     * The ldap configuration base id.
     */
    private static final String LDAP_CONFIGURATION_BASE_ID = "577e8a90400000000000000";

    /**
     * The module base id.
     */
    private static final String MODULE_BASE_ID = "577e8a90500000000000000";

    /**
     * The policy base id.
     */
    private static final String POLICY_BASE_ID = "577e8a90600000000000000";

    /**
     * The system base id.
     */
    private static final String SYSTEM_BASE_ID = "577e8a90700000000000000";

    /**
     * The user base id.
     */
    private static final String USER_BASE_ID = "577e8a90800000000000000";


    /**
     * Generate test data for user.
     *
     * @return the users.
     */
    public static List<User> generateUsers() {
        final List<User> documents = new ArrayList<>();

        int count = 0;
        User document = null;

        document = new User("ro_user1", Role.RO, true);
        document.setId(USER_BASE_ID + (count++));
        documents.add(document);

        document = new User("ro_user2", Role.RO, false);
        document.setId(USER_BASE_ID + (count++));
        documents.add(document);

        document = new User("rw_user1", Role.RW, true);
        document.setId(USER_BASE_ID + (count++));
        documents.add(document);

        document = new User("rw_user2", Role.RW, false);
        document.setId(USER_BASE_ID + (count++));
        documents.add(document);

        document = new User("admin_user1", Role.Admin, true);
        document.setId(USER_BASE_ID + (count++));
        documents.add(document);

        document = new User("admin_user2", Role.Admin, false);
        document.setId(USER_BASE_ID + count);
        documents.add(document);

        return documents;
    }

    /**
     * Generate new test data for user.
     *
     * @return the new user.
     */
    public static User createNewUser() {
        User document = new User("a new user", Role.Admin, true);
        document.setId(USER_BASE_ID + (generateUsers().size() + 1));
        return document;
    }

    /**
     * Generate test data for system.
     *
     * @return the system.
     */
    public static List<System> generateSystems() {
        final List<System> documents = new ArrayList<>();

        for (int num = 1; num <= 4; num++) {
            System document = new System();
            document.setId(SYSTEM_BASE_ID + num);
            document.setName("name " + num);
            document.setDescription("description " + num);
            documents.add(document);
        }

        return documents;
    }

    /**
     * Generate new test data for system.
     *
     * @return the new system.
     */
    public static System createNewSystem() {
        System document = new System();
        document.setId(SYSTEM_BASE_ID + (generateSystems().size() + 1));
        document.setName("new name ");
        document.setDescription("new description ");
        return document;
    }

    /**
     * Generate test data for module.
     *
     * @return the module.
     */
    public static List<Module> generateModules() {
        final List<Module> documents = new ArrayList<>();

        List<System> systems = generateSystems();

        for (int num = 1; num <= 4; num++) {
            Module document = new Module();
            document.setId(MODULE_BASE_ID + num);
            document.setName("name " + num);
            document.setDescription("description " + num);
            document.setSystem(systems.get((num - 1) % 2));
            documents.add(document);
        }

        return documents;
    }

    /**
     * Generate new test data for module.
     *
     * @return the new module.
     */
    public static Module createNewModule() {
        Module document = new Module();
        document.setId(MODULE_BASE_ID + (generateModules().size() + 1));
        document.setName("new name ");
        document.setDescription("new description ");
        document.setSystem(generateSystems().get(0));

        return document;
    }

    /**
     * Generate test data for ldap attribute.
     *
     * @return the ldap attributes.
     */
    public static List<LdapAttribute> generateLdapAttributes() {
        final List<LdapAttribute> documents = new ArrayList<>();

        for (int num = 1; num <= 10; num++) {
            LdapAttribute document = new LdapAttribute();
            document.setId(LDAP_ATTRIBUTE_BASE_ID + num);
            document.setName("name " + num);
            document.setEnabled(num < 5);
            documents.add(document);
        }

        return documents;
    }

    /**
     * Generate test data for ldap configuration.
     *
     * @return the ldap configurations.
     */
    public static List<LdapConfiguration> generateLdapConfigurations() {
        final List<LdapConfiguration> documents = new ArrayList<>();

        for (int num = 1; num <= 4; num++) {
            LdapConfiguration document = new LdapConfiguration();
            document.setId(LDAP_CONFIGURATION_BASE_ID + num);
            document.setName("name " + num);

            document.setEnabled(false);
            document.setUrl("url " + num);
            document.setUserDn("userdn " + num);
            document.setPassword("password " + num);
            document.setUserSearchBase("usersearchbase " + num);
            document.setUserAttribute("userattribute " + num);
            document.setRoot("root " + num);
            documents.add(document);
        }

        return documents;
    }

    /**
     * Generate new test data for ldap configuration.
     *
     * @param config the valid ldap configuration
     * @return the ldap configuration.
     */
    public static LdapConfiguration createValidLdapConfiguration(TestLdapConfig config) {
        LdapConfiguration document = new LdapConfiguration();
        document.setId(LDAP_CONFIGURATION_BASE_ID + (generateLdapConfigurations().size() + 10));
        document.setName("valid ldap config");
        BeanUtils.copyProperties(config, document);
        document.setEnabled(true);
        document.setLastModifiedAt(new Date());
        return document;
    }

    /**
     * Generate new test data for ldap configuration.
     *
     * @return the ldap configuration.
     */
    public static LdapConfiguration createNewLdapConfiguration() {
        LdapConfiguration document = new LdapConfiguration();
        document.setId(LDAP_CONFIGURATION_BASE_ID + (generateLdapConfigurations().size() + 1));
        document.setName("new name");
        document.setUrl("new url");
        document.setUserDn("new username");
        document.setPassword("new password");
        document.setRoot("new root");
        document.setUserSearchBase("new user search base");
        document.setUserAttribute("new user attribute");
        document.setEnabled(false);
        return document;
    }

    /**
     * Generate test data for header.
     *
     * @return the headers.
     */
    public static List<Header> generateHeaders() {
        final List<Header> documents = new ArrayList<>();

        for (int num = 1; num <= 4; num++) {
            Header document = new Header();
            document.setId(HEADER_BASE_ID + num);
            document.setType(HeaderType.DYNAMIC);
            document.setHeaderName("name " + num);
            document.setDescription("description " + num);
            document.setValue("dynamic " + num);
            documents.add(document);
        }

        return documents;
    }

    /**
     * Generate new test data for header.
     *
     * @return the new header.
     */
    public static Header createNewHeader() {
        Header document = new Header();
        document.setId(HEADER_BASE_ID + (generateHeaders().size() + 1));
        document.setHeaderName("new name ");
        document.setType(HeaderType.STATIC);
        document.setDescription("new description ");
        document.setValue("True");
        return document;
    }

    /**
     * Generate test data for policy.
     *
     * @return the policies.
     */
    public static List<Policy> generatePolicies() {
        final List<Policy> documents = new ArrayList<>();

        List<Header> allHeaders = generateHeaders();
        List<Module> modules = generateModules();

        for (int num = 1; num <= 10; num++) {
            Policy document = new Policy();
            document.setId(POLICY_BASE_ID + num);
            document.setName("name " + num);
            document.setDescription("description " + num);
            document.setEnabled(num % 2 == 1);
            document.setModule(modules.get(num % 2));

            List<Rule> rules = new ArrayList<>();

            Rule rule = new Rule();
            rule.setName("rule 1");
            rule.setHeader(allHeaders.get(num % 3));
            List<LdapAttributeNameValue> ruleInfo = new ArrayList<>();
            ruleInfo.add(new LdapAttributeNameValue("attribute 1", "value 1"));
            ruleInfo.add(new LdapAttributeNameValue("attribute 2", "value 2"));
            rule.setRuleInfo(ruleInfo);
            rules.add(rule);

            rule = new Rule();
            rule.setName("rule 2");
            rule.setHeader(allHeaders.get(num % 3 + 1));
            ruleInfo = new ArrayList<>();
            ruleInfo.add(new LdapAttributeNameValue("attribute 2", "value 2"));
            ruleInfo.add(new LdapAttributeNameValue("attribute 3", "value 3"));
            rule.setRuleInfo(ruleInfo);

            rules.add(rule);

            document.setRules(rules);

            documents.add(document);
        }

        return documents;
    }

    /**
     * Generate valid test data for headers match ldap configurations.
     *
     * @return the valid policy.
     */
    public static List<Header> createValidLdapHeaders() {
        int num = 60;
        List<Header> headers = new ArrayList<>();
        Header header1 = new Header();
        header1.setId(HEADER_BASE_ID + (++num));
        header1.setHeaderName("statictrue");
        header1.setType(HeaderType.STATIC);
        header1.setDescription("valid static=true header description");
        header1.setValue("True");
        headers.add(header1);
        Header header2 = new Header();
        header2.setId(HEADER_BASE_ID + (++num));
        header2.setHeaderName("staticfalse");
        header2.setType(HeaderType.STATIC);
        header2.setDescription("valid static=false header description");
        header2.setValue("False");
        headers.add(header2);
        for (String attr : LDAP_ATTRS) {
            Header header = new Header(HeaderType.DYNAMIC, "dynamic_" + attr, "desc " + attr, attr);
            header.setId(HEADER_BASE_ID + (++num));
            headers.add(header);
        }
        return headers;
    }

    /**
     * Generate valid test data for policy match ldap configurations.
     *
     * @return the valid policy.
     */
    public static Policy createValidLdapPolicy() {
        Policy document = new Policy();
        document.setCreatedAt(new Date());
        document.setLastModifiedAt(new Date());
        document.setId(POLICY_BASE_ID + 30);
        document.setName("my valid ldap policy name");
        document.setDescription("valid ldap description");
        document.setEnabled(true);
        Module module = generateModules().get(0);
        document.setModule(module);
        List<Rule> rules = new ArrayList<>();
        int num = 0;
        for (Header header : createValidLdapHeaders()) {
            Rule rule = new Rule();
            rule.setName("valid ldap rule for " + (num++));
            rule.setHeader(header);
            List<LdapAttributeNameValue> ruleInfo = new ArrayList<>();
            ruleInfo.add(new LdapAttributeNameValue("ou", "people"));
            rule.setRuleInfo(ruleInfo);
            rules.add(rule);
        }
        rules.add(rules.get(0));
        document.setRules(rules);
        return document;
    }

    /**
     * Generate valid test data for ldap attribute.
     *
     * @return the valid ldap attributes.
     */
    public static List<LdapAttribute> createValidLdapAttributes() {
        final List<LdapAttribute> documents = new ArrayList<>();

        for (int num = 1; num <= LDAP_ATTRS.length; num++) {
            LdapAttribute document = new LdapAttribute();
            document.setId(LDAP_ATTRIBUTE_BASE_ID + (generateLdapAttributes().size() + 10 + num));
            document.setName(LDAP_ATTRS[num - 1]);
            document.setEnabled(true);
            documents.add(document);
        }

        return documents;
    }

    /**
     * Generate new test data for policy.
     *
     * @return the new policy.
     */
    public static Policy createNewPolicy() {
        Policy document = new Policy();
        document.setId(POLICY_BASE_ID + (generatePolicies().size() + 1));
        document.setName("new name ");
        document.setDescription("new description");
        document.setEnabled(true);
        document.setModule(generateModules().get(0));

        List<Rule> rules = new ArrayList<>();

        Rule rule = new Rule();
        rule.setName("new valid rule 1");
        rule.setHeader(generateHeaders().get(0));
        List<LdapAttributeNameValue> ruleInfo = new ArrayList<>();
        ruleInfo.add(new LdapAttributeNameValue("new attribute 1", "new value 1"));
        ruleInfo.add(new LdapAttributeNameValue("new attribute 2", "new value 2"));
        rule.setRuleInfo(ruleInfo);
        rules.add(rule);

        rule = new Rule();
        rule.setName("new valid rule 2");
        rule.setHeader(generateHeaders().get(1));
        ruleInfo = new ArrayList<>();
        ruleInfo.add(new LdapAttributeNameValue("new attribute 2", "new value 2"));
        ruleInfo.add(new LdapAttributeNameValue("new attribute 3", "new value 3"));
        rule.setRuleInfo(ruleInfo);

        rules.add(rule);

        document.setRules(rules);

        return document;
    }


    /**
     * Generate test data for activity log.
     *
     * @return the activity logs
     */
    public static List<ActivityLog> generateActivityLogs() {
        final List<ActivityLog> documents = new ArrayList<>();

        for (int num = 1; num <= 9; num++) {
            ActivityLog document = new ActivityLog();
            document.setId(ACTIVITY_LOG_BASE_ID + num);
            document.setDescription("description " + num);
            document.setDocumentType(num % 2 == 0 ? "User" : "Policy");
            document.setOperatedBy("username " + num);
            document.setOperationType(num % 3 == 0 ? OperationType.Change : OperationType.Create);
            document.setCreatedAt(new GregorianCalendar(2017, 5, num).getTime());
            document.setLastModifiedAt(new GregorianCalendar(2017, 5, num).getTime());
            documents.add(document);
        }

        return documents;
    }
}
