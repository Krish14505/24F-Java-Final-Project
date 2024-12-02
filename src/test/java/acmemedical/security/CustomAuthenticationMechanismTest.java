package acmemedical.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static jakarta.security.enterprise.identitystore.CredentialValidationResult.Status.VALID;

import java.util.Base64;
import java.util.Collections;

import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for the CustomAuthenticationMechanism class.
 * 
 * @author Harmeet Matharoo
 */
public class CustomAuthenticationMechanismTest {

    private CustomAuthenticationMechanism authMechanism;

    @Mock
    private CustomIdentityStore identityStore;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpMessageContext httpMessageContext;

    @Mock
    private ServletContext servletContext;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authMechanism = new CustomAuthenticationMechanism();
        authMechanism.identityStore = identityStore;
        authMechanism.servletContext = servletContext;
    }

    /**
     * Test validateRequest with valid credentials.
     */
    @Test
    public void testValidateRequest_ValidCredentials() throws Exception {
        String username = "testUser";
        String password = "testPassword";
        String authHeader = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        CredentialValidationResult validationResult = new CredentialValidationResult(username, Collections.singleton("USER"));
        when(identityStore.validate(any(UsernamePasswordCredential.class))).thenReturn(validationResult);
        when(httpMessageContext.notifyContainerAboutLogin(validationResult)).thenReturn(AuthenticationStatus.SUCCESS);

        AuthenticationStatus status = authMechanism.validateRequest(request, response, httpMessageContext);

        assertEquals(AuthenticationStatus.SUCCESS, status, "Authentication should be successful for valid credentials");
        verify(identityStore, times(1)).validate(any(UsernamePasswordCredential.class));
        verify(servletContext, times(1)).log(anyString());
    }

    /**
     * Test validateRequest with invalid credentials.
     */
    @Test
    public void testValidateRequest_InvalidCredentials() throws Exception {
        String username = "testUser";
        String password = "wrongPassword";
        String authHeader = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        CredentialValidationResult validationResult = new CredentialValidationResult(CredentialValidationResult.Status.INVALID);
        when(identityStore.validate(any(UsernamePasswordCredential.class))).thenReturn(validationResult);
        when(httpMessageContext.responseUnauthorized()).thenReturn(AuthenticationStatus.SEND_FAILURE);

        AuthenticationStatus status = authMechanism.validateRequest(request, response, httpMessageContext);

        assertEquals(AuthenticationStatus.SEND_FAILURE, status, "Authentication should fail for invalid credentials");
        verify(identityStore, times(1)).validate(any(UsernamePasswordCredential.class));
        verify(servletContext, never()).log(anyString());
    }

    /**
     * Test validateRequest with no authorization header.
     */
    @Test
    public void testValidateRequest_NoAuthHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(httpMessageContext.doNothing()).thenReturn(AuthenticationStatus.NOT_DONE);

        AuthenticationStatus status = authMechanism.validateRequest(request, response, httpMessageContext);

        assertEquals(AuthenticationStatus.NOT_DONE, status, "Authentication should do nothing if no Authorization header is present");
        verify(identityStore, never()).validate(any(UsernamePasswordCredential.class));
        verify(servletContext, never()).log(anyString());
    }

    /**
     * Test validateRequest with malformed authorization header.
     */
    @Test
    public void testValidateRequest_MalformedAuthHeader() throws Exception {
        String authHeader = "Basic MalformedHeader";
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(httpMessageContext.doNothing()).thenReturn(AuthenticationStatus.NOT_DONE);

        AuthenticationStatus status = authMechanism.validateRequest(request, response, httpMessageContext);

        assertEquals(AuthenticationStatus.NOT_DONE, status, "Authentication should do nothing if Authorization header is malformed");
        verify(identityStore, never()).validate(any(UsernamePasswordCredential.class));
        verify(servletContext, never()).log(anyString());
    }
}
