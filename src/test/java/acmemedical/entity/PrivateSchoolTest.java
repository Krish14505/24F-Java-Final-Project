package acmemedical.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import jakarta.persistence.DiscriminatorValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

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
        assertTrue(privateSchool instanceof MedicalSchool, "PrivateSchool should inherit from MedicalSchool");
    }

    /**
     * Test the discriminator value annotation.
     */
    @Test
    public void testDiscriminatorValue() {
        assertTrue(PrivateSchool.class.isAnnotationPresent(DiscriminatorValue.class), "DiscriminatorValue annotation should be present");
        assertEquals("0", PrivateSchool.class.getAnnotation(DiscriminatorValue.class).value(), "Discriminator value should be '0' for PrivateSchool");
    }

    /**
     * Test JSON type name annotation.
     */
    @Test
    public void testJsonTypeName() {
        assertTrue(PrivateSchool.class.isAnnotationPresent(JsonTypeName.class), "JsonTypeName annotation should be present");
        assertEquals("private", PrivateSchool.class.getAnnotation(JsonTypeName.class).value(), "JsonTypeName value should be 'private'");
    }
}
