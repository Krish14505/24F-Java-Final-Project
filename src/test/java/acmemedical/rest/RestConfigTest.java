package acmemedical.rest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * Unit tests for the RestConfig class.
 * 
 * @author Harmeet Matharoo
 */
public class RestConfigTest {

    /**
     * Test that the `getProperties` method returns the correct configuration.
     */
    @Test
    public void testGetProperties() {
        RestConfig restConfig = new RestConfig();
        Map<String, Object> properties = restConfig.getProperties();

        assertNotNull(properties, "Properties map should not be null");
        assertTrue(properties.containsKey("jersey.config.jsonFeature"), "Properties map should contain 'jersey.config.jsonFeature'");
        assertEquals("JacksonFeature", properties.get("jersey.config.jsonFeature"), 
            "The value of 'jersey.config.jsonFeature' should be 'JacksonFeature'");
    }

    /**
     * Test that `ApplicationPath` is correctly set.
     */
    @Test
    public void testApplicationPath() {
        ApplicationPath applicationPathAnnotation = RestConfig.class.getAnnotation(ApplicationPath.class);
        assertNotNull(applicationPathAnnotation, "RestConfig should have an @ApplicationPath annotation");
        assertEquals("/api", applicationPathAnnotation.value(), "The @ApplicationPath value should be '/api'");
    }

    /**
     * Test that declared roles are present in `@DeclareRoles`.
     */
    @Test
    public void testDeclaredRoles() {
        DeclareRoles declareRolesAnnotation = RestConfig.class.getAnnotation(DeclareRoles.class);
        assertNotNull(declareRolesAnnotation, "RestConfig should have a @DeclareRoles annotation");

        String[] roles = declareRolesAnnotation.value();
        assertArrayEquals(new String[]{"USER", "ADMIN"}, roles, "Declared roles should include 'USER' and 'ADMIN'");
    }
}
