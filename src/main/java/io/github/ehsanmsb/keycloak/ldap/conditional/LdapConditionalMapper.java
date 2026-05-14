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
        boolean skipDisabledUsers = getBooleanConfig(LdapConditionalMapperFactory.SKIP_DISABLED_USERS, true);
        if (skipDisabledUsers && !user.isEnabled()) {
            LOG.debugf("User '%s' is disabled. Skipping conditional group sync.", user.getUsername());
            return;
        }

        String emailAttribute = mapperModel.getConfig().getFirst(LdapConditionalMapperFactory.EMAIL_ATTRIBUTE);
        String emailRegex = mapperModel.getConfig().getFirst(LdapConditionalMapperFactory.EMAIL_REGEX);
        String ldapAttributesRegex = mapperModel.getConfig().getFirst(LdapConditionalMapperFactory.LDAP_ATTRIBUTES_REGEX);
        String groupPath = mapperModel.getConfig().getFirst(LdapConditionalMapperFactory.GROUP_PATH);
        boolean ignoreCase = Boolean.parseBoolean(
            mapperModel.getConfig().getFirst(LdapConditionalMapperFactory.IGNORE_CASE)
        );

        if (isBlank(emailAttribute) || isBlank(emailRegex) || isBlank(ldapAttributesRegex) || isBlank(groupPath)) {
            return;
        }

        String emailValue = ldapUser.getAttributeAsString(emailAttribute);
        if (!isRegexMatch(emailValue, emailRegex, ignoreCase, user.getUsername())) {
            return;
        }

        if (!ExpressionEvaluator.isRegexMatch(ldapUser, ldapAttributesRegex, ignoreCase, LOG, mapperModel.getName(), user.getUsername())) {
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
                "Added user '%s' to group '%s' based on LDAP attributes regex '%s'",
                user.getUsername(), groupPath, ldapAttributesRegex
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

    private boolean isRegexMatch(String value, String regex, boolean ignoreCase, String username) {
        if (value == null) {
            return false;
        }

        try {
            return ExpressionEvaluator.compilePattern(regex, ignoreCase).matcher(value).matches();
        } catch (IllegalArgumentException ex) {
            LOG.warnf(
                "Invalid email regex '%s' for mapper '%s'. User '%s' skipped.",
                regex, mapperModel.getName(), username
            );
            return false;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean getBooleanConfig(String key, boolean defaultValue) {
        String rawValue = mapperModel.getConfig().getFirst(key);
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(rawValue);
    }
}
