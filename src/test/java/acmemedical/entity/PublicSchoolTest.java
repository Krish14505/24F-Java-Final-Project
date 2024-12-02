package acmemedical.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the PublicSchool entity.
 * 
 * @author Harmeet Matharoo
 */
public class PublicSchoolTest {

    /**
     * Test the default constructor and inherited behavior.
     */
    @Test
    public void testDefaultConstructor() {
        PublicSchool publicSchool = new PublicSchool();
        assertNotNull(publicSchool, "PublicSchool object should not be null");
        assertTrue(publicSchool.isPublic(), "PublicSchool should have isPublic set to true");
    }

    /**
     * Test the discriminator value.
     */
    @Test
    public void testDiscriminatorValue() {
        PublicSchool publicSchool = new PublicSchool();
        assertTrue(publicSchool.getClass().isAnnotationPresent(DiscriminatorValue.class), "DiscriminatorValue annotation should be present");
        assertEquals("1", publicSchool.getClass().getAnnotation(DiscriminatorValue.class).value(), "Discriminator value should be '1' for PublicSchool");
    }

    /**
     * Test JSON type name annotation.
     */
    @Test
    public void testJsonTypeName() {
        PublicSchool publicSchool = new PublicSchool();
        assertTrue(publicSchool.getClass().isAnnotationPresent(JsonTypeName.class), "JsonTypeName annotation should be present");
        assertEquals("public", publicSchool.getClass().getAnnotation(JsonTypeName.class).value(), "JsonTypeName value should be 'public'");
    }
}
