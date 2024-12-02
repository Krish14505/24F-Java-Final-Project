package acmemedical.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the MedicalCertificate entity.
 *
 * @author Harmeet Matharoo
 */
public class MedicalCertificateTest {

    private MedicalCertificate medicalCertificate;
    private MedicalTraining medicalTraining;
    private Physician physician;

    @BeforeEach
    public void setUp() {
        medicalTraining = new MedicalTraining();
        physician = new Physician();
        medicalCertificate = new MedicalCertificate(medicalTraining, physician, (byte) 1);
    }

    /**
     * Test the default constructor.
     */
    @Test
    public void testDefaultConstructor() {
        MedicalCertificate certificate = new MedicalCertificate();
        assertNotNull(certificate, "MedicalCertificate object should not be null");
    }

    /**
     * Test the parameterized constructor.
     */
    @Test
    public void testParameterizedConstructor() {
        assertNotNull(medicalCertificate.getMedicalTraining(), "MedicalTraining should be initialized");
        assertNotNull(medicalCertificate.getOwner(), "Physician (owner) should be initialized");
        assertEquals(1, medicalCertificate.getSigned(), "Signed status should match the provided value");
    }

    /**
     * Test setting and getting MedicalTraining.
     */
    @Test
    public void testMedicalTraining() {
        MedicalTraining newTraining = new MedicalTraining();
        medicalCertificate.setMedicalTraining(newTraining);
        assertEquals(newTraining, medicalCertificate.getMedicalTraining(), "MedicalTraining should match the set value");
    }

    /**
     * Test setting and getting Physician (Owner).
     */
    @Test
    public void testPhysicianOwner() {
        Physician newPhysician = new Physician();
        medicalCertificate.setOwner(newPhysician);
        assertEquals(newPhysician, medicalCertificate.getOwner(), "Physician owner should match the set value");
    }

    /**
     * Test setting and getting the signed status (byte value).
     */
    @Test
    public void testSignedByte() {
        medicalCertificate.setSigned((byte) 0);
        assertEquals(0, medicalCertificate.getSigned(), "Signed status should match the set value");
    }

    /**
     * Test setting and getting the signed status (boolean value).
     */
    @Test
    public void testSignedBoolean() {
        medicalCertificate.setSigned(true);
        assertEquals(1, medicalCertificate.getSigned(), "Signed status should be 1 for true input");

        medicalCertificate.setSigned(false);
        assertEquals(0, medicalCertificate.getSigned(), "Signed status should be 0 for false input");
    }

    /**
     * Test equality of MedicalCertificate objects.
     */
    @Test
    public void testEquality() {
        MedicalCertificate otherCertificate = new MedicalCertificate(medicalTraining, physician, (byte) 1);
        assertEquals(medicalCertificate, otherCertificate, "Two MedicalCertificate objects with the same data should be equal");
    }

    /**
     * Test hash code consistency.
     */
    @Test
    public void testHashCode() {
        MedicalCertificate otherCertificate = new MedicalCertificate(medicalTraining, physician, (byte) 1);
        assertEquals(medicalCertificate.hashCode(), otherCertificate.hashCode(), "Hash codes for identical objects should match");
    }
}
