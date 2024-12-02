package acmemedical.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the PojoBaseCompositeKey class.
 *
 * @author Harmeet Matharoo
 */
public class PojoBaseCompositeKeyTest {

    private static class TestPojoBaseCompositeKey extends PojoBaseCompositeKey<String> {
        private String id;

        @Override
        public String getId() {
            return id;
        }

        @Override
        public void setId(String id) {
            this.id = id;
        }
    }

    private TestPojoBaseCompositeKey pojo;

    @BeforeEach
    public void setUp() {
        pojo = new TestPojoBaseCompositeKey();
        pojo.setId("TEST_ID");
        pojo.setVersion(1);
        pojo.setCreated(LocalDateTime.of(2023, 12, 1, 10, 0));
        pojo.setUpdated(LocalDateTime.of(2023, 12, 2, 12, 0));
    }

    /**
     * Test the default constructor.
     */
    @Test
    public void testDefaultConstructor() {
        TestPojoBaseCompositeKey newPojo = new TestPojoBaseCompositeKey();
        assertNotNull(newPojo, "PojoBaseCompositeKey object should not be null");
    }

    /**
     * Test setting and getting the ID.
     */
    @Test
    public void testId() {
        pojo.setId("NEW_TEST_ID");
        assertEquals("NEW_TEST_ID", pojo.getId(), "ID should match the updated value");
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
        TestPojoBaseCompositeKey otherPojo = new TestPojoBaseCompositeKey();
        otherPojo.setId("TEST_ID");
        assertEquals(pojo.hashCode(), otherPojo.hashCode(), "Hash codes for identical objects should match");
    }

    /**
     * Test equals method for equality.
     */
    @Test
    public void testEquals() {
        TestPojoBaseCompositeKey otherPojo = new TestPojoBaseCompositeKey();
        otherPojo.setId("TEST_ID");
        assertEquals(pojo, otherPojo, "Two PojoBaseCompositeKey objects with the same ID should be equal");
    }

    /**
     * Test equals method for inequality.
     */
    @Test
    public void testNotEquals() {
        TestPojoBaseCompositeKey otherPojo = new TestPojoBaseCompositeKey();
        otherPojo.setId("DIFFERENT_ID");
        assertNotEquals(pojo, otherPojo, "Two PojoBaseCompositeKey objects with different IDs should not be equal");
    }
}
