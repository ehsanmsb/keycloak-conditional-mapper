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

    public static final String PROVIDER_ID = "ldap-conditional-group-mapper";
    public static final String LDAP_ATTRIBUTE = "ldap.attribute";
    public static final String EXPECTED_VALUE = "ldap.expected.value";
    public static final String GROUP_PATH = "keycloak.group.path";
    public static final String IGNORE_CASE = "match.ignore.case";

    private static final List<ProviderConfigProperty> CONFIG_PROPERTIES;

    static {
        List<ProviderConfigProperty> properties = new ArrayList<>();

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
        return "Adds imported LDAP users to a Keycloak group if an LDAP attribute matches a configured value.";
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
