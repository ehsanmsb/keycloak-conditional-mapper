# Home

Welcome to the wiki for `keycloak-conditional-mapper`.

This plugin adds imported LDAP users to a Keycloak group when both conditions match:
- Email attribute matches `Email Regex`
- LDAP attributes payload matches `LDAP Attributes Regex`

## Version compatibility

| Mapper Version | Supported Keycloak Version | Status |
| --- | --- | --- |
| `v1.0.0` | `26.4.5` | Supported |

## Quick start

1. Build:

```bash
mvn clean package
```

2. Copy the JAR to Keycloak providers:
- Container: `/opt/keycloak/providers/`
- Local: `<keycloak-home>/providers/`

3. Rebuild and start Keycloak:

```bash
bin/kc.sh build
bin/kc.sh start
```

4. Add mapper `keycloak-conditional-mapper` in LDAP federation and configure:
- `Email Attribute Key`
- `Email Regex`
- `LDAP Attributes Regex`
- `Keycloak Group Path`
- `Case-Insensitive Match`
- `Skip Disabled Users` (default `true`)

## Wiki pages

- [LDAP Attributes Regex](LDAP-Attributes-Regex)
- [OpenLDAP Test Setup](OpenLDAP-Test-Setup)
- [LDIF Test Data (`users.ldif`)](users.ldif)

## Provider ID

`keycloak-conditional-mapper`
