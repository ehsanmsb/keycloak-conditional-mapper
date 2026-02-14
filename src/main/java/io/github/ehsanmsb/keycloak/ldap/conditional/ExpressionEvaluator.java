package io.github.ehsanmsb.keycloak.ldap.conditional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.jboss.logging.Logger;
import org.keycloak.storage.ldap.idm.model.LDAPObject;

public final class ExpressionEvaluator {

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
        Map<String, Set<String>> attributes = ldapUser.getAttributes();
        if (attributes == null || attributes.isEmpty()) {
            return "";
        }

        List<String> names = new ArrayList<>(attributes.keySet());
        Collections.sort(names);

        StringBuilder payload = new StringBuilder();
        for (String name : names) {
            Collection<String> rawValues = attributes.get(name);
            if (rawValues == null || rawValues.isEmpty()) {
                payload.append(name).append("=\n");
                continue;
            }

            List<String> values = new ArrayList<>(rawValues);
            Collections.sort(values);
            for (String value : values) {
                payload.append(name).append("=").append(value == null ? "" : value).append('\n');
            }
        }
        return payload.toString();
    }
}
