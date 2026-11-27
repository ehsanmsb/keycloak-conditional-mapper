package io.github.ehsanmsb.keycloak.ldap.conditional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.keycloak.storage.ldap.idm.model.LDAPDn;
import org.keycloak.storage.ldap.idm.model.LDAPObject;

class ExpressionEvaluatorTest {

    @Test
    void serializesMultilineValuesAsOneLinePerAttributeValue() {
        LDAPObject user = new LDAPObject();
        user.setSingleAttribute("description", "East\r\nBuilding\u2028Floor");
        user.setAttribute("departmentNumber", Set.of("operations", "engineering"));

        String payload = ExpressionEvaluator.buildAttributesPayload(user);

        assertEquals(
            "departmentNumber=engineering\ndepartmentNumber=operations\ndescription=East\\nBuilding\\nFloor\n",
            payload
        );
    }

    @Test
    void preservesDnRuleMatching() {
        LDAPObject user = new LDAPObject();
        user.setDn(LDAPDn.fromString("uid=user01,ou=engineering,dc=example,dc=org"));

        String payload = ExpressionEvaluator.buildAttributesPayload(user);

        assertTrue(ExpressionEvaluator.compilePattern(
            "(?m)^LDAP_ENTRY_DN=[^\r\n]*,ou=engineering,dc=example,dc=org$", false
        ).matcher(payload).find());
    }
}
