package io.github.ehsanmsb.keycloak.ldap.conditional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.jboss.logging.Logger;
import org.keycloak.storage.ldap.idm.model.LDAPDn;
import org.keycloak.storage.ldap.idm.model.LDAPObject;

public final class ExpressionEvaluator {

    private static final Pattern LINE_BREAKS = Pattern.compile("\\R");
    private static final String ESCAPED_LINE_BREAK = Matcher.quoteReplacement("\\n");

    private ExpressionEvaluator() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isRegexMatch(
        LDAPObject ldapUser,
        String attributesRegex,
        boolean ignoreCase,
        Logger log,
        String mapperName,
        String username
    ) {
        String payload = buildAttributesPayload(ldapUser);
        try {
            return compilePattern(attributesRegex, ignoreCase).matcher(payload).find();
        } catch (IllegalArgumentException ex) {
            log.warnf(
                "Invalid LDAP attributes regex '%s' for mapper '%s'. User '%s' skipped.",
                attributesRegex, mapperName, username
            );
            return false;
        }
    }

    public static Pattern compilePattern(String regex, boolean ignoreCase) {
        int flags = Pattern.DOTALL;
        if (ignoreCase) {
            flags |= Pattern.CASE_INSENSITIVE;
        }
        try {
            return Pattern.compile(regex, flags);
        } catch (PatternSyntaxException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    static String buildAttributesPayload(LDAPObject ldapUser) {
        StringBuilder payload = new StringBuilder();

        // Include LDAP_ENTRY_DN (the Distinguished Name) if available
        LDAPDn ldapDn = ldapUser.getDn();
        if (ldapDn != null) {
            String dn = ldapDn.toString();
            if (!dn.isEmpty()) {
                appendAttribute(payload, "LDAP_ENTRY_DN", dn);
            }
        }

        Map<String, Set<String>> attributes = ldapUser.getAttributes();
        if (attributes == null || attributes.isEmpty()) {
            return payload.toString();
        }

        List<String> names = new ArrayList<>(attributes.keySet());
        Collections.sort(names);

        for (String name : names) {
            Collection<String> rawValues = attributes.get(name);
            if (rawValues == null || rawValues.isEmpty()) {
                appendAttribute(payload, name, "");
                continue;
            }

            List<String> values = new ArrayList<>(rawValues);
            Collections.sort(values);
            for (String value : values) {
                appendAttribute(payload, name, value);
            }
        }
        return payload.toString();
    }

    private static void appendAttribute(StringBuilder payload, String name, String value) {
        // Preserve the line boundary between attributes even when a value contains line breaks.
        String singleLine = value == null ? "" : LINE_BREAKS.matcher(value).replaceAll(ESCAPED_LINE_BREAK);
        payload.append(name).append('=').append(singleLine).append('\n');
    }
}
