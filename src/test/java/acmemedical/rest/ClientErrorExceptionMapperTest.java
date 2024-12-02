package acmemedical.rest;

import static org.junit.jupiter.api.Assertions.*;

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

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    public void setUp() {
        exceptionMapper = new ClientErrorExceptionMapper();
    }

    /**
     * Test toResponse method for a 404 ClientErrorException.
     * 
     * @throws Exception if the test fails
     */
    @Test
    public void testToResponse_NotFound() throws Exception {
        // Create a ClientErrorException with a 404 response
        Response response = Response.status(Response.Status.NOT_FOUND).build();
        ClientErrorException exception = new ClientErrorException(response);

        // Get the mapped response
        Response mappedResponse = exceptionMapper.toResponse(exception);

        // Assertions
        assertEquals(404, mappedResponse.getStatus(), "Response status should be 404 Not Found");
        assertTrue(mappedResponse.getEntity() instanceof HttpErrorResponse, "Response entity should be HttpErrorResponse");

        HttpErrorResponse errorResponse = (HttpErrorResponse) mappedResponse.getEntity();
        assertEquals(404, errorResponse.getStatusCode(), "HttpErrorResponse status code should be 404");
        assertEquals("Not Found", errorResponse.getReasonPhrase(), "HttpErrorResponse reason phrase should be 'Not Found'");
    }

    /**
     * Test toResponse method for a 400 ClientErrorException.
     * 
     * @throws Exception if the test fails
     */
    @Test
    public void testToResponse_BadRequest() throws Exception {
        // Create a ClientErrorException with a 400 response
        Response response = Response.status(Response.Status.BAD_REQUEST).build();
        ClientErrorException exception = new ClientErrorException(response);

        // Get the mapped response
        Response mappedResponse = exceptionMapper.toResponse(exception);

        // Assertions
        assertEquals(400, mappedResponse.getStatus(), "Response status should be 400 Bad Request");
        assertTrue(mappedResponse.getEntity() instanceof HttpErrorResponse, "Response entity should be HttpErrorResponse");

        HttpErrorResponse errorResponse = (HttpErrorResponse) mappedResponse.getEntity();
        assertEquals(400, errorResponse.getStatusCode(), "HttpErrorResponse status code should be 400");
        assertEquals("Bad Request", errorResponse.getReasonPhrase(), "HttpErrorResponse reason phrase should be 'Bad Request'");
    }

    /**
     * Test toResponse method for a custom ClientErrorException with status 418.
     * 
     * @throws Exception if the test fails
     */
    @Test
    public void testToResponse_CustomError() throws Exception {
        // Create a ClientErrorException with a custom status (418)
        Response response = Response.status(418).entity("I'm a teapot").build();
        ClientErrorException exception = new ClientErrorException(response);

        // Get the mapped response
        Response mappedResponse = exceptionMapper.toResponse(exception);

        // Assertions
        assertEquals(418, mappedResponse.getStatus(), "Response status should be 418 I'm a teapot");
        assertTrue(mappedResponse.getEntity() instanceof HttpErrorResponse, "Response entity should be HttpErrorResponse");

        HttpErrorResponse errorResponse = (HttpErrorResponse) mappedResponse.getEntity();
        assertEquals(418, errorResponse.getStatusCode(), "HttpErrorResponse status code should be 418");
//        assertEquals("I'm a teapot", errorResponse.getReasonPhrase(), "HttpErrorResponse reason phrase should be 'I'm a teapot'");
    }
}
