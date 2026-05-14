# keycloak-conditional-mapper

![License](https://img.shields.io/github/license/ehsanmsb/keycloak-ldap-conditional-mapper)
![Test](https://github.com/ehsanmsb/keycloak-ldap-conditional-mapper/actions/workflows/checks.yaml/badge.svg?branch=main)
![Release](https://github.com/ehsanmsb/keycloak-ldap-conditional-mapper/actions/workflows/build-release.yaml/badge.svg)
![Tag](https://img.shields.io/github/v/tag/ehsanmsb/keycloak-ldap-conditional-mapper?&logo=git)

Custom Keycloak LDAP mapper that adds imported LDAP users to a Keycloak group when email regex and LDAP attributes regex match.

## Version compatibility

| Mapper Version | Supported Keycloak Version | Status |
| --- | --- | --- |
| `v1.0.0` | `26.4.5` | Supported |

## What it does

- Runs during LDAP user import/sync (`onImportUserFromLDAP`).
- Can skip disabled users (configurable with `Skip Disabled Users`, default `true`).
- Checks email first using a configured LDAP/AD email attribute key and regex.
- Builds a normalized payload of LDAP attributes in `attribute=value` lines.
- Evaluates one regex against that payload (case-sensitive or case-insensitive).
- Adds the user to the configured Keycloak group path only if both checks match.

## Build

```bash
mvn clean package
```

The output jar is created at:

`target/keycloak-conditional-mapper-<semantic-version>.jar`

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
- `LDAP Attributes Regex`: regex evaluated against all LDAP attributes in `attribute=value` lines.
- `Keycloak Group Path`: target group path (example: `/employees/engineering`).
- `Case-Insensitive Match`: `true` or `false`.
- `Skip Disabled Users`: `true` or `false` (default `true`).

## Wiki

- [LDAP Attributes Regex Guide](docs/wiki/LDAP-Attributes-Regex.md)
- [OpenLDAP Test Setup](docs/wiki/OpenLDAP-Test-Setup.md)

## Provider ID

`keycloak-conditional-mapper`
