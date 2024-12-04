package acmemedical.rest.resource;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the HttpErrorResponse class.
 * 
 * @author Harmeet Matharoo
 */
public class HttpErrorResponseTest {

    /**
     * Test constructor and getters for HttpErrorResponse.
     */
    @Test
    public void testConstructorAndGetters() {
        int statusCode = 404;
        String reasonPhrase = "Not Found";

        HttpErrorResponse errorResponse = new HttpErrorResponse(statusCode, reasonPhrase);

        assertNotNull(errorResponse, "HttpErrorResponse object should not be null");
        assertEquals(statusCode, errorResponse.getStatusCode(), "Status code should match the provided value");
        assertEquals(reasonPhrase, errorResponse.getReasonPhrase(), "Reason phrase should match the provided value");
    }

    /**
     * Test JSON property annotations.
     */
    @Test
    public void testJsonPropertyAnnotations() throws NoSuchMethodException {
        var statusCodeGetter = HttpErrorResponse.class.getMethod("getStatusCode");
        var reasonPhraseGetter = HttpErrorResponse.class.getMethod("getReasonPhrase");

        assertTrue(statusCodeGetter.isAnnotationPresent(com.fasterxml.jackson.annotation.JsonProperty.class),
                "getStatusCode should have @JsonProperty annotation");
        assertEquals("status-code", statusCodeGetter.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class).value(),
                "getStatusCode @JsonProperty value should be 'status-code'");

        assertTrue(reasonPhraseGetter.isAnnotationPresent(com.fasterxml.jackson.annotation.JsonProperty.class),
                "getReasonPhrase should have @JsonProperty annotation");
        assertEquals("reason-phrase", reasonPhraseGetter.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class).value(),
                "getReasonPhrase @JsonProperty value should be 'reason-phrase'");
    }
}
