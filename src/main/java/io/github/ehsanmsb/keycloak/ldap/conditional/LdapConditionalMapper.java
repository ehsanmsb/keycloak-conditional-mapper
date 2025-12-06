package io.github.ehsanmsb.keycloak.ldap.conditional;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.*;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.idm.model.LDAPObject;
import org.keycloak.storage.ldap.idm.query.internal.LDAPQuery;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapper;
import org.keycloak.storage.ldap.mappers.LDAPStorageMapper;

public class LdapConditionalMapper extends AbstractLDAPStorageMapper implements LDAPStorageMapper {

    protected static final String CONFIG_REGEX = "regex.attribute";
    protected static final String CONFIG_ASSIGNMENT_TYPE = "assignment.type"; // role | group
    protected static final String CONFIG_ROLE_NAME = "role.name";
    protected static final String CONFIG_CLIENT_ID = "client.id";
    protected static final String CONFIG_REALM = "realm.name";
    protected static final String CONFIG_GROUP_PATH = "group.path";

    public LdapConditionalMapper(LDAPStorageProvider ldapProvider, ComponentModel model) {
        super(model, ldapProvider);
    }

    public void onImportUserFromLDAP(LDAPObject ldapUser, UserModel user, RealmModel realm, ComponentModel mapperModel, LDAPStorageProvider ldapProvider) {

    }

    @Override
    public void onImportUserFromLDAP(LDAPObject ldapObject, UserModel userModel, RealmModel realmModel, boolean b) {

    }

    @Override
    public void onRegisterUserToLDAP(LDAPObject ldapObject, UserModel userModel, RealmModel realmModel) {

    }

    @Override
    public UserModel proxy(LDAPObject ldapObject, UserModel userModel, RealmModel realmModel) {
        return null;
    }

    @Override
    public void beforeLDAPQuery(LDAPQuery ldapQuery) {

    }
}
