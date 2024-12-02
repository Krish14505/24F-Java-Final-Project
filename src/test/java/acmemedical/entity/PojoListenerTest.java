package acmemedical.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the PojoListener class.
 * 
 * @author Harmeet Matharoo
 */
public class PojoListenerTest {

    // Concrete subclass of PojoBase for testing
    private static class TestPojoBase extends PojoBase {}

    private PojoListener listener;
    private TestPojoBase entity;

    @BeforeEach
    public void setUp() {
        listener = new PojoListener();
        entity = new TestPojoBase();
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
