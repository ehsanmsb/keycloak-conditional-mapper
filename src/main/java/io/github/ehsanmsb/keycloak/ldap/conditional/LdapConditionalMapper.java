package io.github.ehsanmsb.keycloak.ldap.conditional;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.idm.model.LDAPObject;
import org.keycloak.storage.ldap.idm.query.internal.LDAPQuery;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapper;

public class LdapConditionalMapper extends AbstractLDAPStorageMapper {

    private static final Logger LOG = Logger.getLogger(LdapConditionalMapper.class);

    public LdapConditionalMapper(ComponentModel mapperModel, LDAPStorageProvider ldapProvider) {
        super(mapperModel, ldapProvider);
    }

    @Override
    public void onImportUserFromLDAP(LDAPObject ldapUser, UserModel user, RealmModel realm, boolean isCreate) {
        String ldapAttribute = mapperModel.getConfig().getFirst(LdapConditionalMapperFactory.LDAP_ATTRIBUTE);
        String expectedValue = mapperModel.getConfig().getFirst(LdapConditionalMapperFactory.EXPECTED_VALUE);
        String groupPath = mapperModel.getConfig().getFirst(LdapConditionalMapperFactory.GROUP_PATH);

        if (isBlank(ldapAttribute) || isBlank(expectedValue) || isBlank(groupPath)) {
            return;
        }

        String attributeValue = ldapUser.getAttributeAsString(ldapAttribute);
        if (!isMatch(attributeValue, expectedValue)) {
            return;
        }

        KeycloakSession session = ldapProvider.getSession();
        GroupModel targetGroup = KeycloakModelUtils.findGroupByPath(session, realm, groupPath);

        if (targetGroup == null) {
            LOG.warnf("Conditional LDAP mapper group path not found: %s", groupPath);
            return;
        }

        if (!user.isMemberOf(targetGroup)) {
            user.joinGroup(targetGroup);
            LOG.debugf(
                "Added user '%s' to group '%s' based on LDAP attribute '%s' value '%s'",
                user.getUsername(), groupPath, ldapAttribute, attributeValue
            );
        }
    }

    @Override
    public void onRegisterUserToLDAP(LDAPObject ldapUser, UserModel user, RealmModel realm) {
        // No outbound LDAP writes required for this mapper.
    }

    @Override
    public void beforeLDAPQuery(LDAPQuery ldapQuery) {
        // No query customization required for this mapper.
    }

    @Override
    public UserModel proxy(LDAPObject ldapUser, UserModel delegate, RealmModel realm) {
        return delegate;
    }

    private boolean isMatch(String attributeValue, String expectedValue) {
        if (attributeValue == null) {
            return false;
        }

        boolean ignoreCase = Boolean.parseBoolean(
            mapperModel.getConfig().getFirst(LdapConditionalMapperFactory.IGNORE_CASE)
        );
        return ignoreCase
            ? attributeValue.equalsIgnoreCase(expectedValue)
            : attributeValue.equals(expectedValue);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
