package acmemedical.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import acmemedical.entity.SecurityRole;
import acmemedical.entity.SecurityUser;

/**
 * Unit tests for the CustomIdentityStoreJPAHelper class.
 * 
 * @author Harmeet Matharoo
 */
public class CustomIdentityStoreJPAHelperTest {

    private CustomIdentityStoreJPAHelper identityStoreHelper;

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<SecurityUser> query;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        identityStoreHelper = new CustomIdentityStoreJPAHelper();
        identityStoreHelper.em = em;
    }

    /**
     * Test findUserByName with a valid username.
     */
    @Test
    public void testFindUserByName_ValidUsername() {
        String username = "testUser";
        SecurityUser mockUser = new SecurityUser();
        mockUser.setUsername(username);

        when(em.createNamedQuery("SecurityUser.userByName", SecurityUser.class)).thenReturn(query);
        when(query.setParameter("param1", username)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(mockUser);

        SecurityUser result = identityStoreHelper.findUserByName(username);

        assertNotNull(result, "User should not be null");
        assertEquals(username, result.getUsername(), "Usernames should match");
        verify(em, times(1)).createNamedQuery("SecurityUser.userByName", SecurityUser.class);
        verify(query, times(1)).setParameter("param1", username);
        verify(query, times(1)).getSingleResult();
    }

    /**
     * Test findUserByName with a non-existent username.
     */
    @Test
    public void testFindUserByName_InvalidUsername() {
        String username = "nonExistentUser";

        when(em.createNamedQuery("SecurityUser.userByName", SecurityUser.class)).thenReturn(query);
        when(query.setParameter("param1", username)).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new jakarta.persistence.NoResultException());

        SecurityUser result = identityStoreHelper.findUserByName(username);

        assertNull(result, "Result should be null for non-existent user");
        verify(em, times(1)).createNamedQuery("SecurityUser.userByName", SecurityUser.class);
        verify(query, times(1)).setParameter("param1", username);
        verify(query, times(1)).getSingleResult();
    }

    /**
     * Test findRoleNamesForUser with roles.
     */
    @Test
    public void testFindRoleNamesForUser() {
        String username = "testUser";
        SecurityUser mockUser = new SecurityUser();
        mockUser.setUsername(username);

        SecurityRole role1 = new SecurityRole();
        role1.setRoleName("ROLE_ADMIN");
        SecurityRole role2 = new SecurityRole();
        role2.setRoleName("ROLE_USER");

        Set<SecurityRole> roles = new HashSet<>();
        roles.add(role1);
        roles.add(role2);
        mockUser.setRoles(roles);

        when(em.createNamedQuery("SecurityUser.userByName", SecurityUser.class)).thenReturn(query);
        when(query.setParameter("param1", username)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(mockUser);

        Set<String> result = identityStoreHelper.findRoleNamesForUser(username);

        assertNotNull(result, "Role names should not be null");
        assertEquals(2, result.size(), "There should be two roles");
        assertTrue(result.contains("ROLE_ADMIN"), "Role names should contain 'ROLE_ADMIN'");
        assertTrue(result.contains("ROLE_USER"), "Role names should contain 'ROLE_USER'");
    }

    /**
     * Test saveSecurityUser.
     */
    @Test
    public void testSaveSecurityUser() {
        SecurityUser user = new SecurityUser();
        user.setUsername("testUser");

        identityStoreHelper.saveSecurityUser(user);

        verify(em, times(1)).persist(user);
    }

    /**
     * Test saveSecurityRole.
     */
    @Test
    public void testSaveSecurityRole() {
        SecurityRole role = new SecurityRole();
        role.setRoleName("ROLE_USER");

        identityStoreHelper.saveSecurityRole(role);

        verify(em, times(1)).persist(role);
    }
}
