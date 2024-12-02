package acmemedical.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the PojoCompositeListener class.
 * 
 * @author Harmeet Matharoo
 */
public class PojoCompositeListenerTest {

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

    private PojoCompositeListener listener;
    private TestPojoBaseCompositeKey entity;

    @BeforeEach
    public void setUp() {
        listener = new PojoCompositeListener();
        entity = new TestPojoBaseCompositeKey();
    }

    /**
     * Test the setCreatedOnDate method for PrePersist functionality.
     */
    @Test
    public void testSetCreatedOnDate() {
        listener.setCreatedOnDate(entity);

        assertNotNull(entity.getCreated(), "Created date should not be null after PrePersist");
        assertNotNull(entity.getUpdated(), "Updated date should not be null after PrePersist");
        assertEquals(entity.getCreated(), entity.getUpdated(), "Created and Updated dates should match on PrePersist");
    }

    /**
     * Test the setUpdatedDate method for PreUpdate functionality.
     */
    @Test
    public void testSetUpdatedDate() {
        LocalDateTime initialCreated = LocalDateTime.of(2023, 12, 1, 10, 0);
        entity.setCreated(initialCreated);
        entity.setUpdated(initialCreated);

        listener.setUpdatedDate(entity);

        assertNotNull(entity.getUpdated(), "Updated date should not be null after PreUpdate");
        assertTrue(entity.getUpdated().isAfter(initialCreated), "Updated date should be after the initial created date");
    }
}
