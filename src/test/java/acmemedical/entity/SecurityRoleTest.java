package acmemedical.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests for the SecurityRole entity.
 * 
 * @author Harmeet Matharoo
 */
public class SecurityRoleTest {

    private SecurityRole securityRole;
    private SecurityUser user1;
    private SecurityUser user2;

    @BeforeEach
    public void setUp() {
        // Initialize SecurityRole
        securityRole = new SecurityRole();
        securityRole.setId(1);
        securityRole.setRoleName("ADMIN");

        // Initialize SecurityUser instances
        user1 = new SecurityUser();
        user1.setId(100);
        user1.setUsername("user1");

        user2 = new SecurityUser();
        user2.setId(101);
        user2.setUsername("user2");

        // Add users to the role
        Set<SecurityUser> users = new HashSet<>();
        users.add(user1);
        users.add(user2);

        securityRole.setUsers(users);
    }

    /**
     * Test default constructor.
     */
    @Test
    public void testDefaultConstructor() {
        SecurityRole newRole = new SecurityRole();
        assertNotNull(newRole, "SecurityRole object should not be null");
    }

    /**
     * Test setting and getting the ID.
     */
    @Test
    public void testId() {
        securityRole.setId(2);
        assertEquals(2, securityRole.getId(), "ID should match the updated value");
    }

    /**
     * Test setting and getting the role name.
     */
    @Test
    public void testRoleName() {
        securityRole.setRoleName("USER");
        assertEquals("USER", securityRole.getRoleName(), "Role name should match the updated value");
    }

    /**
     * Test the Many-to-Many relationship with SecurityUser.
     */
    @Test
    public void testUsers() {
        Set<SecurityUser> users = securityRole.getUsers();
        assertNotNull(users, "Users set should not be null");
        assertEquals(2, users.size(), "Users set should contain the correct number of entries");
        assertTrue(users.contains(user1), "Users set should contain user1");
        assertTrue(users.contains(user2), "Users set should contain user2");

        // Test adding a new user
        SecurityUser user3 = new SecurityUser();
        user3.setId(102);
        user3.setUsername("user3");

        securityRole.addUserToRole(user3);
        assertTrue(securityRole.getUsers().contains(user3), "New user should be added to the users set");
    }

    /**
     * Test hashCode method.
     */
    @Test
    public void testHashCode() {
        SecurityRole otherRole = new SecurityRole();
        otherRole.setId(1);
        assertEquals(securityRole.hashCode(), otherRole.hashCode(), "Hash codes for identical objects should match");
    }

    /**
     * Test equals method for equality.
     */
    @Test
    public void testEquals() {
        SecurityRole otherRole = new SecurityRole();
        otherRole.setId(1);
        assertEquals(securityRole, otherRole, "Two SecurityRole objects with the same ID should be equal");
    }

    /**
     * Test equals method for inequality.
     */
    @Test
    public void testNotEquals() {
        SecurityRole otherRole = new SecurityRole();
        otherRole.setId(2);
        assertNotEquals(securityRole, otherRole, "Two SecurityRole objects with different IDs should not be equal");
    }

    /**
     * Test the toString method.
     */
    @Test
    public void testToString() {
        String expected = "SecurityRole [id = 1, roleName = ADMIN]";
        assertEquals(expected, securityRole.toString(), "toString method should return the expected value");
    }
}
