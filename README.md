# keycloak-conditional-mapper

![License](https://img.shields.io/github/license/ehsanmsb/keycloak-ldap-conditional-mapper)
![Test](https://github.com/ehsanmsb/keycloak-ldap-conditional-mapper/actions/workflows/checks.yaml/badge.svg?branch=main)
![Release](https://github.com/ehsanmsb/keycloak-ldap-conditional-mapper/actions/workflows/build-release.yaml/badge.svg)
![Tag](https://img.shields.io/github/v/tag/ehsanmsb/keycloak-ldap-conditional-mapper?&logo=git)

Custom Keycloak LDAP mapper that adds imported LDAP users to a Keycloak group when email regex and LDAP attributes regex match.

## Version compatibility

| Mapper Version | Supported Keycloak Version | Status |
| --- | --- | --- |
| Current source | `26.8.2` | Target |
| `v1.2.2` | `26.4.5` | Previous target |

## What it does

- Runs during LDAP user import/sync (`onImportUserFromLDAP`).
- Can skip disabled users (configurable with `Skip Disabled Users`, default `true`).
- Checks email first using a configured LDAP/AD email attribute key and regex.
- Builds a normalized payload of LDAP attributes in `attribute=value` lines.
- Evaluates one regex against that payload (case-sensitive or case-insensitive).
- Adds the user to the configured Keycloak group path only if both checks match.

## Build

Use a patched JDK 21, such as Eclipse Temurin 21.0.13.1+2 or a later security update.

```bash
mvn clean package
```

The output jar is created at:

`target/keycloak-conditional-mapper-<semantic-version>.jar`

## Install in Keycloak

For Keycloak 26.8.2, use the official container image `quay.io/keycloak/keycloak:26.8.2-3` or a supported JVM with the vendor fix for [CVE-2026-114938](https://www.keycloak.org/2026/11/java4shell-vulnerability-cve-2026-114938). For an existing deployment, follow the [Keycloak upgrade guide](https://www.keycloak.org/docs/26.8.2/upgrading/) before replacing the provider JAR.

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
- `LDAP Attributes Regex`: regex evaluated against all LDAP attributes in `attribute=value` lines. For access rules, use complete lines such as `(?m)^departmentNumber=engineering$` and LDAP attributes that users cannot modify.
- `Keycloak Group Path`: target group path (example: `/employees/engineering`).
- `Case-Insensitive Match`: `true` or `false`.
- `Skip Disabled Users`: `true` or `false` (default `true`).

The mapper adds memberships during import or sync. Review and remove unintended existing memberships in Keycloak when tightening a rule.

## Wiki

- [LDAP Attributes Regex Guide](docs/wiki/LDAP-Attributes-Regex.md)
- [OpenLDAP Test Setup](docs/wiki/OpenLDAP-Test-Setup.md)

## Provider ID

`keycloak-conditional-mapper`
