package acmemedical.rest.serializer;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import acmemedical.entity.SecurityRole;

/**
 * Unit tests for the SecurityRoleSerializer class.
 * 
 * @author Harmeet Matharoo
 */
public class SecurityRoleSerializerTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(new SecurityRoleSerializer());
        objectMapper.registerModule(module);
    }

    /**
     * Test serialization of SecurityRole objects.
     */
    @Test
    public void testSerializeSecurityRoles() throws JsonProcessingException {
        // Create mock SecurityRoles
        SecurityRole role1 = new SecurityRole();
        role1.setId(1);
        role1.setRoleName("ADMIN");
        role1.setUsers(null); // Mimic the hollow role behavior

        SecurityRole role2 = new SecurityRole();
        role2.setId(2);
        role2.setRoleName("USER");
        role2.setUsers(null); // Mimic the hollow role behavior

        Set<SecurityRole> roles = new HashSet<>();
        roles.add(role1);
        roles.add(role2);

        // Serialize the roles set
        String json = objectMapper.writeValueAsString(roles);

        // Validate JSON structure
        assertTrue(json.contains("\"id\":1"), "JSON should contain role ID 1");
        assertTrue(json.contains("\"roleName\":\"ADMIN\""), "JSON should contain role name 'ADMIN'");
        assertTrue(json.contains("\"id\":2"), "JSON should contain role ID 2");
        assertTrue(json.contains("\"roleName\":\"USER\""), "JSON should contain role name 'USER'");
        assertFalse(json.contains("users"), "JSON should not contain 'users' field");
    }

    /**
     * Test serialization with an empty set of SecurityRoles.
     */
    @Test
    public void testSerializeEmptyRoles() throws JsonProcessingException {
        Set<SecurityRole> roles = new HashSet<>();

        // Serialize the empty set
        String json = objectMapper.writeValueAsString(roles);

        // Validate JSON structure
        assertEquals("[]", json, "JSON for an empty set should be '[]'");
    }

    /**
     * Test serialization with a null set of SecurityRoles.
     */
    @Test
    public void testSerializeNullRoles() throws IOException {
        // Serialize a null value
        String json = objectMapper.writeValueAsString(null);

        // Validate JSON structure
        assertEquals("null", json, "JSON for a null set should be 'null'");
    }
}
