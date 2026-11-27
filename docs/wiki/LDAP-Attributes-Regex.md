# LDAP Attributes Regex (Keycloak Conditional Mapper)

This page explains how to write the value for the **`LDAP Attributes Regex`** field in `keycloak-conditional-mapper`.

## How matching works

- The mapper builds one text payload from all LDAP attributes.
- Each attribute value becomes one line. Line breaks in values are represented by the literal text `\n`. The format is:

```text
attributeName=value
```

- Example payload:

```text
cn=John Doe
departmentNumber=engineering
mail=john.doe@example.com
title=Senior Engineer
```

- Your regex is evaluated against that full payload using Java regex.
- The matcher uses `find()` (partial match), not full-string `matches()`.
- The regex is compiled with `DOTALL`, so `.` can match across line breaks.
- If **Case-Insensitive Match** is enabled, matching is case-insensitive.

## Quick patterns

- Match a single attribute/value:

```regex
(?m)^departmentNumber=engineering$
```

- OR condition:

```regex
(?m)^(?:departmentNumber=engineering|departmentNumber=marketing)$
```

- AND condition (lookaheads):

```regex
(?m)\A(?=.*^departmentNumber=engineering$)(?=.*^title=Senior Engineer$).*
```

- AND + OR:

```regex
(?m)\A(?=.*^(?:departmentNumber=engineering|departmentNumber=platform)$)(?=.*^title=Senior Engineer$).*
```

## Exact line matching

Because the matcher uses `find()`, plain patterns match anywhere in the payload.

For access rules, use LDAP attributes that users cannot modify and match complete lines with multiline anchors:

```regex
(?m)^departmentNumber=engineering$
```

Notes:
- `(?m)` enables line-based `^` and `$`.
- Without `(?m)`, `^` and `$` refer to the start/end of the full payload.

## Escaping special characters

Regex characters must be escaped when you want literal text.

- Literal dot in value:

```regex
(?m)^mail=[^\r\n]*@example\.com$
```

- Literal parentheses:

```regex
(?m)^description=Team \(Platform\)$
```

Common regex special characters: `. ^ $ * + ? ( ) [ ] { } | \`

## Multi-valued attributes

If an LDAP attribute has multiple values, each value appears on its own line:

```text
memberOf=cn=dev,ou=groups,dc=example,dc=com
memberOf=cn=ops,ou=groups,dc=example,dc=com
```

Match any `memberOf` value:

```regex
(?m)^memberOf=cn=dev,ou=groups,dc=example,dc=com$
```

## Special attributes

### LDAP_ENTRY_DN

The Distinguished Name (DN) of the LDAP entry is automatically included in the payload as `LDAP_ENTRY_DN`:

```text
cn=John Doe
LDAP_ENTRY_DN=uid=jdoe,ou=users,dc=example,dc=com
departmentNumber=engineering
mail=john.doe@example.com
title=Senior Engineer
```

You can use this in your regex to match users based on their DN structure:

```regex
(?m)^LDAP_ENTRY_DN=[^\r\n]*,ou=engineering,dc=example,dc=com$
```

## Practical recipes

- User must be in engineering and senior:

```regex
(?m)\A(?=.*^departmentNumber=engineering$)(?=.*^title=senior$).*
```

- User in engineering or marketing, and located in berlin:

```regex
(?m)\A(?=.*^(?:departmentNumber=engineering|departmentNumber=marketing)$)(?=.*^l=berlin$).*
```

- Match group DN safely (commas are literal, no escape needed):

```regex
(?m)^memberOf=cn=platform,ou=groups,dc=example,dc=com$
```

## Troubleshooting

- No users match:
  - Check spelling and exact LDAP attribute names.
  - Confirm whether **Case-Insensitive Match** is on/off.
  - If you expect exact line matching, add `(?m)^...$`.
- Regex is invalid:
  - Mapper logs a warning and skips the user.
  - Validate your regex syntax with Java regex rules.
- Unexpected broad matches:
  - Remember `find()` matches any substring.
  - Use multiline anchors for exact attributes and values. In values, use `[^\r\n]*` for a wildcard that stays on the same line.

## Reference behavior in this project

- Payload builder: `src/main/java/io/github/ehsanmsb/keycloak/ldap/conditional/ExpressionEvaluator.java`
- Mapper config field label: `src/main/java/io/github/ehsanmsb/keycloak/ldap/conditional/LdapConditionalMapperFactory.java`
