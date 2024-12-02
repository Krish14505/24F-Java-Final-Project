package acmemedical.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the PojoBase class.
 * 
 * @author Harmeet Matharoo
 */
public class PojoBaseTest {

    // Concrete subclass for testing
    private static class TestPojoBase extends PojoBase {}

    private TestPojoBase pojo;

    @BeforeEach
    public void setUp() {
        pojo = new TestPojoBase();
        pojo.setId(1);
        pojo.setVersion(1);
        pojo.setCreated(LocalDateTime.of(2023, 12, 1, 10, 0));
        pojo.setUpdated(LocalDateTime.of(2023, 12, 2, 12, 0));
    }

    /**
     * Test the default constructor.
     */
    @Test
    public void testDefaultConstructor() {
        TestPojoBase newPojo = new TestPojoBase();
        assertNotNull(newPojo, "PojoBase object should not be null");
    }

    /**
     * Test setting and getting the ID.
     */
    @Test
    public void testId() {
        pojo.setId(2);
        assertEquals(2, pojo.getId(), "ID should match the updated value");
    }

    /**
     * Test setting and getting the version.
     */
    @Test
    public void testVersion() {
        pojo.setVersion(2);
        assertEquals(2, pojo.getVersion(), "Version should match the updated value");
    }

    /**
     * Test setting and getting the created timestamp.
     */
    @Test
    public void testCreatedTimestamp() {
        LocalDateTime newCreated = LocalDateTime.of(2023, 12, 5, 8, 0);
        pojo.setCreated(newCreated);
        assertEquals(newCreated, pojo.getCreated(), "Created timestamp should match the updated value");
    }

    /**
     * Test setting and getting the updated timestamp.
     */
    @Test
    public void testUpdatedTimestamp() {
        LocalDateTime newUpdated = LocalDateTime.of(2023, 12, 6, 9, 0);
        pojo.setUpdated(newUpdated);
        assertEquals(newUpdated, pojo.getUpdated(), "Updated timestamp should match the updated value");
    }

    /**
     * Test hashCode consistency.
     */
    @Test
    public void testHashCode() {
        TestPojoBase otherPojo = new TestPojoBase();
        otherPojo.setId(1);
        assertEquals(pojo.hashCode(), otherPojo.hashCode(), "Hash codes for identical objects should match");
    }

    /**
     * Test equals method for equality.
     */
    @Test
    public void testEquals() {
        TestPojoBase otherPojo = new TestPojoBase();
        otherPojo.setId(1);
        assertEquals(pojo, otherPojo, "Two PojoBase objects with the same ID should be equal");
    }

    /**
     * Test equals method for inequality.
     */
    @Test
    public void testNotEquals() {
        TestPojoBase otherPojo = new TestPojoBase();
        otherPojo.setId(2);
        assertNotEquals(pojo, otherPojo, "Two PojoBase objects with different IDs should not be equal");
    }
}
