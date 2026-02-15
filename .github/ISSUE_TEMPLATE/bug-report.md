---
name: Bug report
about: Report a problem with keycloak-conditional-mapper
title: "[Bug]: "
labels: bug
assignees: ""
---

## Summary

Describe the issue clearly.

## Expected behavior

Describe what you expected to happen.

## Environment

- Mapper version:
- Keycloak version:
- Java version:

## Mapper configuration

```yaml
emailAttributeKey: mail
emailRegex: ".*@example\\.org"
ldapAttributesRegex: "(?=.*departmentNumber=engineering).*"
groupPath: /engineering
caseInsensitiveMatch: true
```

## Steps to reproduce

1.
2.
3.

## Relevant logs

Paste Keycloak logs related to the mapper.

## Additional context

Add extra context, screenshots, or sample LDAP attribute payload.
