package acmemedical.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests for the SecurityUser entity.
 * 
 * @author Harmeet Matharoo
 */
public class SecurityUserTest {

    private SecurityUser securityUser;
    private SecurityRole role1;
    private SecurityRole role2;
    private Physician physician;

    @BeforeEach
    public void setUp() {
        // Initialize SecurityUser
        securityUser = new SecurityUser();
        securityUser.setId(1);
        securityUser.setUsername("testUser");
        securityUser.setPwHash("passwordHash");

        // Initialize SecurityRoles
        role1 = new SecurityRole();
        role1.setId(10);
        role1.setRoleName("ADMIN");

        role2 = new SecurityRole();
        role2.setId(11);
        role2.setRoleName("USER");

        // Initialize Physician
        physician = new Physician();
        physician.setId(100);
        physician.setFirstName("John");
        physician.setLastName("Doe");

        // Add roles to the SecurityUser
        Set<SecurityRole> roles = new HashSet<>();
        roles.add(role1);
        roles.add(role2);

        securityUser.setRoles(roles);
        securityUser.setPhysician(physician);
    }

    /**
     * Test the default constructor.
     */
    @Test
    public void testDefaultConstructor() {
        SecurityUser newUser = new SecurityUser();
        assertNotNull(newUser, "SecurityUser object should not be null");
    }

    /**
     * Test setting and getting the ID.
     */
    @Test
    public void testId() {
        securityUser.setId(2);
        assertEquals(2, securityUser.getId(), "ID should match the updated value");
    }

    /**
     * Test setting and getting the username.
     */
    @Test
    public void testUsername() {
        securityUser.setUsername("newUser");
        assertEquals("newUser", securityUser.getUsername(), "Username should match the updated value");
    }

    /**
     * Test setting and getting the password hash.
     */
    @Test
    public void testPwHash() {
        securityUser.setPwHash("newHash");
        assertEquals("newHash", securityUser.getPwHash(), "Password hash should match the updated value");
    }

    /**
     * Test setting and getting the roles.
     */
    @Test
    public void testRoles() {
        Set<SecurityRole> roles = securityUser.getRoles();
        assertNotNull(roles, "Roles set should not be null");
        assertEquals(2, roles.size(), "Roles set should contain the correct number of entries");
        assertTrue(roles.contains(role1), "Roles set should contain role1");
        assertTrue(roles.contains(role2), "Roles set should contain role2");

        SecurityRole role3 = new SecurityRole();
        role3.setId(12);
        role3.setRoleName("GUEST");
        roles.add(role3);

        securityUser.setRoles(roles);
        assertTrue(securityUser.getRoles().contains(role3), "New role should be added to the roles set");
    }

    /**
     * Test the relationship with Physician.
     */
    @Test
    public void testPhysician() {
        assertEquals(physician, securityUser.getPhysician(), "Physician should match the set value");

        Physician newPhysician = new Physician();
        newPhysician.setId(200);
        securityUser.setPhysician(newPhysician);
        assertEquals(newPhysician, securityUser.getPhysician(), "Physician should update correctly");
    }

    /**
     * Test the getName method from Principal interface.
     */
    @Test
    public void testGetName() {
        assertEquals("testUser", securityUser.getName(), "Name should match the username");
    }

    /**
     * Test hashCode consistency.
     */
    @Test
    public void testHashCode() {
        SecurityUser otherUser = new SecurityUser();
        otherUser.setId(1);
        assertEquals(securityUser.hashCode(), otherUser.hashCode(), "Hash codes for identical objects should match");
    }

    /**
     * Test equals method for equality.
     */
    @Test
    public void testEquals() {
        SecurityUser otherUser = new SecurityUser();
        otherUser.setId(1);
        assertEquals(securityUser, otherUser, "Two SecurityUser objects with the same ID should be equal");
    }

    /**
     * Test equals method for inequality.
     */
    @Test
    public void testNotEquals() {
        SecurityUser otherUser = new SecurityUser();
        otherUser.setId(2);
        assertNotEquals(securityUser, otherUser, "Two SecurityUser objects with different IDs should not be equal");
    }

    /**
     * Test the toString method.
     */
    @Test
    public void testToString() {
        String expected = "SecurityUser [id = 1, username = testUser]";
        assertEquals(expected, securityUser.toString(), "toString method should return the expected value");
    }
}
