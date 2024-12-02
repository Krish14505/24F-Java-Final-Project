package acmemedical.utility;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response.Status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import acmemedical.rest.resource.HttpErrorResponse;

/**
 * Unit tests for the HttpErrorAsJSONServlet class.
 * 
 * @author Harmeet Matharoo
 */
public class HttpErrorAsJSONServletTest {

    private HttpErrorAsJSONServlet servlet;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        servlet = new HttpErrorAsJSONServlet();
        objectMapper = new ObjectMapper();
        HttpErrorAsJSONServlet.setObjectMapper(objectMapper);
    }

    /**
     * Test the servlet for non-error HTTP status codes.
     */
    @Test
    public void testService_NonErrorStatus() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(response.getStatus()).thenReturn(Status.OK.getStatusCode());

        servlet.service(request, response);

        // Verify that the superclass `service` method was called
        verify(response, never()).getWriter();
    }

    /**
     * Test the servlet for error HTTP status codes.
     */
    @Test
    public void testService_ErrorStatus() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(response.getStatus()).thenReturn(Status.BAD_REQUEST.getStatusCode());
        when(response.getWriter()).thenReturn(printWriter);

        servlet.service(request, response);

        // Capture the JSON response
        printWriter.flush();
        String jsonResponse = stringWriter.toString();

        // Verify that the content type is set to application/json
        verify(response).setContentType("application/json");

        // Parse the JSON response and validate
        HttpErrorResponse errorResponse = objectMapper.readValue(jsonResponse, HttpErrorResponse.class);
        assertEquals(Status.BAD_REQUEST.getStatusCode(), errorResponse.getStatusCode());
        assertEquals(Status.BAD_REQUEST.getReasonPhrase(), errorResponse.getReasonPhrase());
    }

    /**
     * Test the servlet for unrecognized HTTP status codes.
     */
    @Test
    public void testService_UnrecognizedStatus() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        int unrecognizedStatusCode = 999;
        when(response.getStatus()).thenReturn(unrecognizedStatusCode);
        when(response.getWriter()).thenReturn(printWriter);

        servlet.service(request, response);

        // Capture the JSON response
        printWriter.flush();
        String jsonResponse = stringWriter.toString();

        // Verify that the content type is set to application/json
        verify(response).setContentType("application/json");

        // Parse the JSON response and validate
        HttpErrorResponse errorResponse = objectMapper.readValue(jsonResponse, HttpErrorResponse.class);
        assertEquals(unrecognizedStatusCode, errorResponse.getStatusCode());
        assertNull(errorResponse.getReasonPhrase(), "Reason phrase should be null for unrecognized status code");
    }
}
