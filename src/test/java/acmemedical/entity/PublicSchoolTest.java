package acmemedical.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import jakarta.persistence.DiscriminatorValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

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
        assertTrue(publicSchool instanceof MedicalSchool, "PublicSchool should inherit from MedicalSchool");
    }

    /**
     * Test the discriminator value annotation.
     */
    @Test
    public void testDiscriminatorValue() {
        assertTrue(PublicSchool.class.isAnnotationPresent(DiscriminatorValue.class), "DiscriminatorValue annotation should be present");
        assertEquals("1", PublicSchool.class.getAnnotation(DiscriminatorValue.class).value(), "Discriminator value should be '1' for PublicSchool");
    }

    /**
     * Test JSON type name annotation.
     */
    @Test
    public void testJsonTypeName() {
        assertTrue(PublicSchool.class.isAnnotationPresent(JsonTypeName.class), "JsonTypeName annotation should be present");
        assertEquals("public", PublicSchool.class.getAnnotation(JsonTypeName.class).value(), "JsonTypeName value should be 'public'");
    }
}
