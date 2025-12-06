package io.github.ehsanmsb.keycloak.ldap.conditional;

import org.keycloak.component.ComponentModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapper;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapperFactory;

import java.util.List;

public class LdapConditionalMapperFactory extends AbstractLDAPStorageMapperFactory {

    public static final String PROVIDER_ID = "ldap-conditional-mapper";

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getHelpText() {
        return "Evaluates a user's attribute value using a configurable regex. "
                + "When the value matches the regex, the mapper automatically assigns either a realm role, "
                + "a client role, or a group to the user depending on the configuration.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {

        ProviderConfigurationBuilder config = ProviderConfigurationBuilder.create()

                // ─────────────────────────────────
                // Regex Attribute
                // ─────────────────────────────────
                .property()
                .name(LdapConditionalMapper.CONFIG_REGEX)
                .label("User Attribute Regex")
                .helpText("Regex used to evaluate the user's attribute value.")
                .type(ProviderConfigProperty.STRING_TYPE)
                .required(true)
                .add()

                // ─────────────────────────────────
                // Assignment Type (role / group)
                // ─────────────────────────────────
                .property()
                .name(LdapConditionalMapper.CONFIG_ASSIGNMENT_TYPE)
                .label("Assignment Type")
                .helpText("Choose whether to assign a Role or a Group when the regex matches.")
                .type(ProviderConfigProperty.LIST_TYPE)
                .options("role", "group")
                .defaultValue("role")
                .required(true)
                .add()

                // ─────────────────────────────────
                // Realm Role Name
                // ─────────────────────────────────
                .property()
                .name(LdapConditionalMapper.CONFIG_REALM)
                .label("Realm Name")
                .helpText("Realm name containing the target realm role. Leave empty if assigning a Client Role.")
                .type(ProviderConfigProperty.STRING_TYPE)
                .required(false)
                .add()

                .property()
                .name(LdapConditionalMapper.CONFIG_ROLE_NAME)
                .label("Role Name")
                .helpText("Name of the role to assign. For realm roles, specify only the role name. "
                        + "For client roles, specify the client role name.")
                .type(ProviderConfigProperty.STRING_TYPE)
                .required(false)
                .add()

                // ─────────────────────────────────
                // Client ID (Client Role assignment)
                // ─────────────────────────────────
                .property()
                .name(LdapConditionalMapper.CONFIG_CLIENT_ID)
                .label("Client ID (for Client Role)")
                .helpText("Client ID that contains the target role. Leave empty if assigning a Realm Role.")
                .type(ProviderConfigProperty.STRING_TYPE)
                .required(false)
                .add()

                // ─────────────────────────────────
                // Group Path
                // ─────────────────────────────────
                .property()
                .name(LdapConditionalMapper.CONFIG_GROUP_PATH)
                .label("Group Path")
                .helpText("Group path to assign to the user (e.g., /grafana/editors). "
                        + "Only required when Assignment Type = group.")
                .type(ProviderConfigProperty.STRING_TYPE)
                .required(false)
                .add();

        return config.build();
    }


    @Override
    protected AbstractLDAPStorageMapper createMapper(ComponentModel mapperModel, LDAPStorageProvider federationProvider) {
        return new LdapConditionalMapper(mapperModel, federationProvider);
    }
}
