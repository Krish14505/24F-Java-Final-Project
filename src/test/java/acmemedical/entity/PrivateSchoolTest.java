package acmemedical.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the PrivateSchool entity.
 * 
 * @author Harmeet Matharoo
 */
public class PrivateSchoolTest {

    /**
     * Test the default constructor and inherited behavior.
     */
    @Test
    public void testDefaultConstructor() {
        PrivateSchool privateSchool = new PrivateSchool();
        assertNotNull(privateSchool, "PrivateSchool object should not be null");
        assertFalse(privateSchool.isPublic(), "PrivateSchool should have isPublic set to false");
    }

    /**
     * Test the discriminator value.
     */
    @Test
    public void testDiscriminatorValue() {
        PrivateSchool privateSchool = new PrivateSchool();
        assertTrue(privateSchool.getClass().isAnnotationPresent(DiscriminatorValue.class), "DiscriminatorValue annotation should be present");
        assertEquals("0", privateSchool.getClass().getAnnotation(DiscriminatorValue.class).value(), "Discriminator value should be '0' for PrivateSchool");
    }

    /**
     * Test JSON type name annotation.
     */
    @Test
    public void testJsonTypeName() {
        PrivateSchool privateSchool = new PrivateSchool();
        assertTrue(privateSchool.getClass().isAnnotationPresent(JsonTypeName.class), "JsonTypeName annotation should be present");
        assertEquals("private", privateSchool.getClass().getAnnotation(JsonTypeName.class).value(), "JsonTypeName value should be 'private'");
    }
}
