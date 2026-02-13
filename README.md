# keycloak-conditional-mapper

Custom Keycloak LDAP mapper that adds imported LDAP users to a Keycloak group when email regex and LDAP attribute conditions match.

## What it does

- Runs during LDAP user import/sync (`onImportUserFromLDAP`).
- Checks email first using a configured LDAP/AD email attribute key and regex.
- Reads one LDAP attribute from the imported LDAP user.
- Compares it with an expected value (case-sensitive or case-insensitive).
- Adds the user to the configured Keycloak group path only if both checks match.

## Build

```bash
mvn clean package
```

The output jar is created at:

`target/keycloak-conditional-mapper-0.1.0-SNAPSHOT.jar`

## Install in Keycloak

1. Copy the jar to your Keycloak providers directory:
   - Container: `/opt/keycloak/providers/`
   - Local distribution: `<keycloak-home>/providers/`
2. Rebuild Keycloak:
   - `bin/kc.sh build`
3. Start Keycloak:
   - `bin/kc.sh start`

## Mapper configuration

In your LDAP user federation provider, add this mapper and set:

- `Email Attribute Key`: email attribute key (example: `mail` or `userPrincipalName`).
- `Email Regex`: regex to filter users by email (example: `.*@gmail\.com`).
- `LDAP Attribute`: LDAP attribute to evaluate (example: `department`).
- `Expected Attribute Value`: value to match (example: `engineering`).
- `Keycloak Group Path`: target group path (example: `/employees/engineering`).
- `Case-Insensitive Match`: `true` or `false`.

## Provider ID

`keycloak-conditional-mapper`
