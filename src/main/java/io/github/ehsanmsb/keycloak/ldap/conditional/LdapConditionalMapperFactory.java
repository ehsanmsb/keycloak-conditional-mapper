package io.github.ehsanmsb.keycloak.ldap.conditional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.keycloak.component.ComponentModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapper;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapperFactory;

public class LdapConditionalMapperFactory extends AbstractLDAPStorageMapperFactory {

    public static final String PROVIDER_ID = "keycloak-conditional-mapper";
    public static final String EMAIL_ATTRIBUTE = "ldap.email.attribute";
    public static final String EMAIL_REGEX = "ldap.email.regex";
    public static final String LDAP_ATTRIBUTE = "ldap.attribute";
    public static final String EXPECTED_VALUE = "ldap.expected.value";
    public static final String GROUP_PATH = "keycloak.group.path";
    public static final String IGNORE_CASE = "match.ignore.case";

    private static final List<ProviderConfigProperty> CONFIG_PROPERTIES;

    static {
        List<ProviderConfigProperty> properties = new ArrayList<>();

        ProviderConfigProperty emailAttribute = new ProviderConfigProperty();
        emailAttribute.setName(EMAIL_ATTRIBUTE);
        emailAttribute.setLabel("Email Attribute Key");
        emailAttribute.setType(ProviderConfigProperty.STRING_TYPE);
        emailAttribute.setDefaultValue("mail");
        emailAttribute.setHelpText("LDAP/AD attribute key used for email matching (for example: mail or userPrincipalName).");
        properties.add(emailAttribute);

        ProviderConfigProperty emailRegex = new ProviderConfigProperty();
        emailRegex.setName(EMAIL_REGEX);
        emailRegex.setLabel("Email Regex");
        emailRegex.setType(ProviderConfigProperty.STRING_TYPE);
        emailRegex.setHelpText("Only users whose email attribute matches this regex are processed (for example: .*@gmail\\.com).");
        properties.add(emailRegex);

        ProviderConfigProperty ldapAttribute = new ProviderConfigProperty();
        ldapAttribute.setName(LDAP_ATTRIBUTE);
        ldapAttribute.setLabel("LDAP Attribute");
        ldapAttribute.setType(ProviderConfigProperty.STRING_TYPE);
        ldapAttribute.setHelpText("LDAP user attribute that will be evaluated during LDAP import/sync.");
        properties.add(ldapAttribute);

        ProviderConfigProperty expectedValue = new ProviderConfigProperty();
        expectedValue.setName(EXPECTED_VALUE);
        expectedValue.setLabel("Expected Attribute Value");
        expectedValue.setType(ProviderConfigProperty.STRING_TYPE);
        expectedValue.setHelpText("If LDAP attribute equals this value, user is added to the configured group.");
        properties.add(expectedValue);

        ProviderConfigProperty groupPath = new ProviderConfigProperty();
        groupPath.setName(GROUP_PATH);
        groupPath.setLabel("Keycloak Group Path");
        groupPath.setType(ProviderConfigProperty.STRING_TYPE);
        groupPath.setHelpText("Target group path, for example /employees or /departments/engineering.");
        properties.add(groupPath);

        ProviderConfigProperty ignoreCase = new ProviderConfigProperty();
        ignoreCase.setName(IGNORE_CASE);
        ignoreCase.setLabel("Case-Insensitive Match");
        ignoreCase.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        ignoreCase.setDefaultValue("true");
        ignoreCase.setHelpText("When enabled, attribute value matching ignores case.");
        properties.add(ignoreCase);

        CONFIG_PROPERTIES = Collections.unmodifiableList(properties);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getHelpText() {
        return "Adds imported LDAP users to a Keycloak group if email regex and LDAP attribute conditions match.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return CONFIG_PROPERTIES;
    }

    @Override
    protected AbstractLDAPStorageMapper createMapper(ComponentModel model, LDAPStorageProvider ldapProvider) {
        return new LdapConditionalMapper(model, ldapProvider);
    }
}
