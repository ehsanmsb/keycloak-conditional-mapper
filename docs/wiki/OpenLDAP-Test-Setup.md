# OpenLDAP Test Setup

This guide creates a local OpenLDAP container, imports test users from `docs/wiki/users.ldif`, and configures Keycloak LDAP federation for this project.

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
- users under `ou=company` with `departmentNumber` attribute.

```bash
docker cp docs/wiki/users.ldif openldap:/tmp/users.ldif
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

You should see all users from the LDIF with their `departmentNumber` values.

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

- `LDAP Attributes Regex`: for example `departmentNumber=engineering`
- `Keycloak Group Path`: for example `/engineering`
- `Case-Insensitive Match`: `true`

When synchronization runs, users whose LDAP attribute payload matches the regex are added to the configured group.

## 6. Troubleshooting: `Already exists (68)` on `ldapadd`

If you run import multiple times, you may see:

```text
adding new entry "ou=company,dc=example,dc=org"

adding new entry "ou=groups,dc=example,dc=org"
ldap_add: Already exists (68)
```

This means one or more entries in the LDIF already exist in LDAP.

### Solution A: full reset (recommended for tests)

```bash
docker exec -it openldap ldapdelete -x \
  -D "cn=admin,dc=example,dc=org" -w admin \
  -r "ou=company,dc=example,dc=org"

docker exec -it openldap ldapdelete -x \
  -D "cn=admin,dc=example,dc=org" -w admin \
  -r "ou=groups,dc=example,dc=org"
```

Then import again:

```bash
docker cp docs/wiki/users.ldif openldap:/tmp/users.ldif
docker exec -it openldap ldapadd -x \
  -D "cn=admin,dc=example,dc=org" -w admin \
  -f /tmp/users.ldif
```

### Solution B: delete only user entries

List existing users:

```bash
docker exec -it openldap ldapsearch -LLL -x \
  -D "cn=admin,dc=example,dc=org" -w admin \
  -b "ou=company,dc=example,dc=org" "(uid=*)" dn
```

Delete returned user DNs with `ldapdelete`, then run `ldapadd` again.
