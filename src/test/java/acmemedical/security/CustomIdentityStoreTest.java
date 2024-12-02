package acmemedical.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;

import jakarta.security.enterprise.credential.CallerOnlyCredential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import acmemedical.entity.SecurityRole;
import acmemedical.entity.SecurityUser;

/**
 * Unit tests for the CustomIdentityStore class.
 * 
 * @author Harmeet Matharoo
 */
public class CustomIdentityStoreTest {

    private CustomIdentityStore identityStore;

    @Mock
    private CustomIdentityStoreJPAHelper jpaHelper;

    @Mock
    private Pbkdf2PasswordHash passwordHash;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        identityStore = new CustomIdentityStore();
        identityStore.jpaHelper = jpaHelper;
        identityStore.pbAndjPasswordHash = passwordHash;
    }

    /**
     * Test validate with valid UsernamePasswordCredential.
     */
    @Test
    public void testValidate_ValidUsernamePasswordCredential() {
        String username = "testUser";
        String password = "testPassword";
        String hashedPassword = "hashedPassword";

        SecurityUser mockUser = new SecurityUser();
        mockUser.setUsername(username);
        mockUser.setPwHash(hashedPassword);

        SecurityRole role1 = new SecurityRole();
        role1.setRoleName("ROLE_ADMIN");
        SecurityRole role2 = new SecurityRole();
        role2.setRoleName("ROLE_USER");
        Set<SecurityRole> roles = new HashSet<>();
        roles.add(role1);
        roles.add(role2);
        mockUser.setRoles(roles);

        when(jpaHelper.findUserByName(username)).thenReturn(mockUser);
        when(passwordHash.verify(password.toCharArray(), hashedPassword)).thenReturn(true);

        UsernamePasswordCredential credential = new UsernamePasswordCredential(username, password);
        CredentialValidationResult result = identityStore.validate(credential);

        assertNotNull(result, "Result should not be null");
        assertEquals(CredentialValidationResult.Status.VALID, result.getStatus(), "Result status should be VALID");
        assertEquals(username, result.getCallerPrincipal().getName(), "CallerPrincipal name should match username");
        assertTrue(result.getCallerGroups().contains("ROLE_ADMIN"), "CallerGroups should contain ROLE_ADMIN");
        assertTrue(result.getCallerGroups().contains("ROLE_USER"), "CallerGroups should contain ROLE_USER");
    }

    /**
     * Test validate with invalid UsernamePasswordCredential.
     */
    @Test
    public void testValidate_InvalidUsernamePasswordCredential() {
        String username = "testUser";
        String password = "testPassword";

        SecurityUser mockUser = new SecurityUser();
        mockUser.setUsername(username);
        mockUser.setPwHash("wrongHashedPassword");

        when(jpaHelper.findUserByName(username)).thenReturn(mockUser);
        when(passwordHash.verify(password.toCharArray(), "wrongHashedPassword")).thenReturn(false);

        UsernamePasswordCredential credential = new UsernamePasswordCredential(username, password);
        CredentialValidationResult result = identityStore.validate(credential);

        assertEquals(CredentialValidationResult.Status.INVALID, result.getStatus(), "Result status should be INVALID");
    }

    /**
     * Test validate with CallerOnlyCredential.
     */
    @Test
    public void testValidate_ValidCallerOnlyCredential() {
        String username = "testUser";

        SecurityUser mockUser = new SecurityUser();
        mockUser.setUsername(username);

        when(jpaHelper.findUserByName(username)).thenReturn(mockUser);

        CallerOnlyCredential credential = new CallerOnlyCredential(username);
        CredentialValidationResult result = identityStore.validate(credential);

        assertNotNull(result, "Result should not be null");
        assertEquals(CredentialValidationResult.Status.VALID, result.getStatus(), "Result status should be VALID");
        assertEquals(username, result.getCallerPrincipal().getName(), "CallerPrincipal name should match username");
    }

    /**
     * Test validate with invalid CallerOnlyCredential.
     */
    @Test
    public void testValidate_InvalidCallerOnlyCredential() {
        String username = "nonExistentUser";

        when(jpaHelper.findUserByName(username)).thenReturn(null);

        CallerOnlyCredential credential = new CallerOnlyCredential(username);
        CredentialValidationResult result = identityStore.validate(credential);

        assertEquals(CredentialValidationResult.Status.INVALID, result.getStatus(), "Result status should be INVALID");
    }

    /**
     * Test validate with unsupported Credential type.
     */
    @Test
    public void testValidate_UnsupportedCredential() {
        CredentialValidationResult result = identityStore.validate(new Credential() {});
        assertEquals(CredentialValidationResult.Status.INVALID, result.getStatus(), "Result status should be INVALID");
    }
}
