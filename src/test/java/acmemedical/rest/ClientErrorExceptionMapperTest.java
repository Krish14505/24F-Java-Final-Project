package acmemedical.rest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import acmemedical.rest.resource.HttpErrorResponse;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;

/**
 * Unit tests for the ClientErrorExceptionMapper class.
 * 
 * @author Harmeet Matharoo
 */
public class ClientErrorExceptionMapperTest {

    private ClientErrorExceptionMapper exceptionMapper;

    @BeforeEach
    public void setUp() {
        exceptionMapper = new ClientErrorExceptionMapper();
    }

    /**
     * Test toResponse method for a 404 ClientErrorException.
     */
    @Test
    public void testToResponse_NotFound() {
        Response mockResponse = mock(Response.class);
        when(mockResponse.getStatusInfo()).thenReturn(Response.Status.NOT_FOUND);

        ClientErrorException exception = new ClientErrorException(mockResponse);
        Response response = exceptionMapper.toResponse(exception);

        assertEquals(404, response.getStatus(), "Response status should match 404 Not Found");
        assertTrue(response.getEntity() instanceof HttpErrorResponse, "Response entity should be an instance of HttpErrorResponse");

        HttpErrorResponse errorResponse = (HttpErrorResponse) response.getEntity();
        assertEquals(404, errorResponse.getStatusCode(), "HttpErrorResponse status code should match 404");
        assertEquals("Not Found", errorResponse.getReasonPhrase(), "HttpErrorResponse reason phrase should match 'Not Found'");
    }

    /**
     * Test toResponse method for a 400 ClientErrorException.
     */
    @Test
    public void testToResponse_BadRequest() {
        Response mockResponse = mock(Response.class);
        when(mockResponse.getStatusInfo()).thenReturn(Response.Status.BAD_REQUEST);

        ClientErrorException exception = new ClientErrorException(mockResponse);
        Response response = exceptionMapper.toResponse(exception);

        assertEquals(400, response.getStatus(), "Response status should match 400 Bad Request");
        assertTrue(response.getEntity() instanceof HttpErrorResponse, "Response entity should be an instance of HttpErrorResponse");

        HttpErrorResponse errorResponse = (HttpErrorResponse) response.getEntity();
        assertEquals(400, errorResponse.getStatusCode(), "HttpErrorResponse status code should match 400");
        assertEquals("Bad Request", errorResponse.getReasonPhrase(), "HttpErrorResponse reason phrase should match 'Bad Request'");
    }

    /**
     * Test toResponse method for a custom ClientErrorException.
     */
    @Test
    public void testToResponse_CustomError() {
        Response mockResponse = mock(Response.class);
        Response.StatusType customStatus = mock(Response.StatusType.class);
        when(customStatus.getStatusCode()).thenReturn(418);
        when(customStatus.getReasonPhrase()).thenReturn("I'm a teapot");
        when(mockResponse.getStatusInfo()).thenReturn(customStatus);

        ClientErrorException exception = new ClientErrorException(mockResponse);
        Response response = exceptionMapper.toResponse(exception);

        assertEquals(418, response.getStatus(), "Response status should match 418 I'm a teapot");
        assertTrue(response.getEntity() instanceof HttpErrorResponse, "Response entity should be an instance of HttpErrorResponse");

        HttpErrorResponse errorResponse = (HttpErrorResponse) response.getEntity();
        assertEquals(418, errorResponse.getStatusCode(), "HttpErrorResponse status code should match 418");
        assertEquals("I'm a teapot", errorResponse.getReasonPhrase(), "HttpErrorResponse reason phrase should match 'I'm a teapot'");
    }
}
