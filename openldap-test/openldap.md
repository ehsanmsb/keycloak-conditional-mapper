# OpenLDAP Test Setup

This guide creates a local OpenLDAP container, imports 10 test users from `users.ldif`, and configures Keycloak LDAP federation for this project.

## 1. Run OpenLDAP

```bash
docker run -d \
  --name openldap \
  -p 389:389 \
  -e LDAP_ORGANISATION="Example Inc" \
  -e LDAP_DOMAIN="example.org" \
  -e LDAP_ADMIN_PASSWORD="admin" \
  osixia/openldap:1.5.0
```

## 2. Import test users

The LDIF creates:

- `ou=company,dc=example,dc=org`
- `ou=groups,dc=example,dc=org`
- 10 users under `ou=company` with `departmentNumber` attribute.

```bash
docker cp openldap-test/users.ldif openldap:/tmp/users.ldif
docker exec -it openldap ldapadd -x \
  -D "cn=admin,dc=example,dc=org" \
  -w admin \
  -f /tmp/users.ldif
```

## 3. Verify users are present

```bash
docker exec -it openldap ldapsearch -x \
  -D "cn=admin,dc=example,dc=org" \
  -w admin \
  -b "ou=company,dc=example,dc=org" \
  "(objectClass=inetOrgPerson)" uid departmentNumber
```

You should see `uid=user01` through `uid=user10` with their `departmentNumber` values.

## 4. Configure LDAP federation in Keycloak

Use these values in your LDAP provider:

- `Vendor`: `other`
- `Connection URL`: `ldap://<your-openldap-host>:389`
- `Bind DN`: `cn=admin,dc=example,dc=org`
- `Bind Credential`: `admin`
- `Edit mode`: `READ_ONLY`
- `Import Users`: `ON`
- `Sync Registrations`: `OFF`

User settings:

- `Users DN`: `ou=company,dc=example,dc=org`
- `Search Scope`: `Subtree`
- `Username LDAP attribute`: `uid`
- `RDN LDAP attribute`: `uid`
- `UUID LDAP attribute`: `entryUUID`
- `User object classes`: `inetOrgPerson,organizationalPerson,person,top`
- `Custom User LDAP Filter`: empty (or `(objectClass=inetOrgPerson)`)

Then click `Synchronize all users`.

## 5. Configure this conditional mapper plugin

Add mapper `keycloak-conditional-mapper` and set:

- `LDAP Attribute`: `departmentNumber`
- `Expected Attribute Value`: for example `engineering`
- `Keycloak Group Path`: for example `/engineering`
- `Case-Insensitive Match`: `true`

When synchronization runs, users with matching `departmentNumber` are added to the configured group.
