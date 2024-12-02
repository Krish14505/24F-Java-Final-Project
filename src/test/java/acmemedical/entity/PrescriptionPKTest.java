package acmemedical.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the PrescriptionPK class.
 * 
 * @author Harmeet Matharoo
 */
public class PrescriptionPKTest {

    private PrescriptionPK prescriptionPK;

    @BeforeEach
    public void setUp() {
        prescriptionPK = new PrescriptionPK(1, 2);
    }

    /**
     * Test the default constructor.
     */
    @Test
    public void testDefaultConstructor() {
        PrescriptionPK newPK = new PrescriptionPK();
        assertNotNull(newPK, "PrescriptionPK object should not be null");
    }

    /**
     * Test the parameterized constructor.
     */
    @Test
    public void testParameterizedConstructor() {
        assertEquals(1, prescriptionPK.getPhysicianId(), "Physician ID should match the initialized value");
        assertEquals(2, prescriptionPK.getPatientId(), "Patient ID should match the initialized value");
    }

    /**
     * Test setting and getting the physician ID.
     */
    @Test
    public void testPhysicianId() {
        prescriptionPK.setPhysicianId(10);
        assertEquals(10, prescriptionPK.getPhysicianId(), "Physician ID should match the updated value");
    }

    /**
     * Test setting and getting the patient ID.
     */
    @Test
    public void testPatientId() {
        prescriptionPK.setPatientId(20);
        assertEquals(20, prescriptionPK.getPatientId(), "Patient ID should match the updated value");
    }

    /**
     * Test the equals method for equality.
     */
    @Test
    public void testEquals() {
        PrescriptionPK otherPK = new PrescriptionPK(1, 2);
        assertEquals(prescriptionPK, otherPK, "Two PrescriptionPK objects with the same data should be equal");
    }

    /**
     * Test the equals method for inequality.
     */
    @Test
    public void testNotEquals() {
        PrescriptionPK otherPK = new PrescriptionPK(3, 4);
        assertNotEquals(prescriptionPK, otherPK, "Two PrescriptionPK objects with different data should not be equal");
    }

    /**
     * Test hashCode consistency.
     */
    @Test
    public void testHashCode() {
        PrescriptionPK otherPK = new PrescriptionPK(1, 2);
        assertEquals(prescriptionPK.hashCode(), otherPK.hashCode(), "Hash codes for identical objects should match");
    }

    /**
     * Test the toString method.
     */
    @Test
    public void testToString() {
        String expected = "PrescriptionPK [physicianId = 1, patientId = 2]";
        assertEquals(expected, prescriptionPK.toString(), "toString method should return the expected value");
    }
}
