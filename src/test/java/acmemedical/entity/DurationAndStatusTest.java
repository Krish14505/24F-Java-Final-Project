package acmemedical.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the DurationAndStatus class.
 *
 * @author Harmeet Matharoo
 */
public class DurationAndStatusTest {

    private DurationAndStatus durationAndStatus;

    @BeforeEach
    public void setUp() {
        durationAndStatus = new DurationAndStatus();
    }

    /**
     * Test the default constructor.
     */
    @Test
    public void testDefaultConstructor() {
        assertNotNull(durationAndStatus, "DurationAndStatus object should not be null");
    }

    /**
     * Test setting and getting the start date.
     */
    @Test
    public void testStartDate() {
        LocalDateTime startDate = LocalDateTime.of(2023, 12, 1, 10, 0);
        durationAndStatus.setStartDate(startDate);
        assertEquals(startDate, durationAndStatus.getStartDate(), "Start date should match the set value");
    }

    /**
     * Test setting and getting the end date.
     */
    @Test
    public void testEndDate() {
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 31, 18, 0);
        durationAndStatus.setEndDate(endDate);
        assertEquals(endDate, durationAndStatus.getEndDate(), "End date should match the set value");
    }

    /**
     * Test setting and getting the active status.
     */
    @Test
    public void testActiveStatus() {
        byte active = 1;
        durationAndStatus.setActive(active);
        assertEquals(active, durationAndStatus.getActive(), "Active status should match the set value");
    }

    /**
     * Test the setDurationAndStatus method.
     */
    @Test
    public void testSetDurationAndStatus() {
        LocalDateTime startDate = LocalDateTime.of(2023, 12, 1, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 31, 18, 0);
        durationAndStatus.setDurationAndStatus(startDate, endDate, "+");
        assertEquals(startDate, durationAndStatus.getStartDate(), "Start date should be set correctly");
        assertEquals(endDate, durationAndStatus.getEndDate(), "End date should be set correctly");
        assertEquals(1, durationAndStatus.getActive(), "Active status should be 1 for '+' input");
    }

    /**
     * Test the equals method.
     */
    @Test
    public void testEquals() {
        DurationAndStatus other = new DurationAndStatus();
        other.setStartDate(LocalDateTime.of(2023, 12, 1, 10, 0));
        other.setEndDate(LocalDateTime.of(2023, 12, 31, 18, 0));
        other.setActive((byte) 1);

        durationAndStatus.setStartDate(LocalDateTime.of(2023, 12, 1, 10, 0));
        durationAndStatus.setEndDate(LocalDateTime.of(2023, 12, 31, 18, 0));
        durationAndStatus.setActive((byte) 1);

        assertTrue(durationAndStatus.equals(other), "Two DurationAndStatus objects with the same data should be equal");
    }

}
