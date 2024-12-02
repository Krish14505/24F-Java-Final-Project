package acmemedical.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the MedicalTraining entity.
 *
 * @author Harmeet Matharoo
 */
public class MedicalTrainingTest {

    private MedicalTraining medicalTraining;
    private MedicalSchool medicalSchool;
    private MedicalCertificate medicalCertificate;
    private DurationAndStatus durationAndStatus;

    @BeforeEach
    public void setUp() {
        medicalTraining = new MedicalTraining();

        medicalSchool = new PublicSchool();
        medicalSchool.setName("Test Medical School");

        medicalCertificate = new MedicalCertificate();
        durationAndStatus = new DurationAndStatus();
        durationAndStatus.setStartDate(java.time.LocalDateTime.of(2023, 12, 1, 9, 0));
        durationAndStatus.setEndDate(java.time.LocalDateTime.of(2024, 5, 30, 17, 0));
        durationAndStatus.setActive((byte) 1);

        medicalTraining.setMedicalSchool(medicalSchool);
        medicalTraining.setCertificate(medicalCertificate);
        medicalTraining.setDurationAndStatus(durationAndStatus);
    }

    /**
     * Test the default constructor.
     */
    @Test
    public void testDefaultConstructor() {
        MedicalTraining newTraining = new MedicalTraining();
        assertNotNull(newTraining, "MedicalTraining object should not be null");
        assertNotNull(newTraining.getDurationAndStatus(), "DurationAndStatus should be initialized in the constructor");
    }

    /**
     * Test setting and getting the medical school.
     */
    @Test
    public void testMedicalSchool() {
        assertEquals(medicalSchool, medicalTraining.getMedicalSchool(), "Medical school should match the set value");

        MedicalSchool newSchool = new PrivateSchool();
        newSchool.setName("New Medical School");
        medicalTraining.setMedicalSchool(newSchool);
        assertEquals(newSchool, medicalTraining.getMedicalSchool(), "Medical school should update correctly");
    }

    /**
     * Test setting and getting the medical certificate.
     */
    @Test
    public void testMedicalCertificate() {
        assertEquals(medicalCertificate, medicalTraining.getCertificate(), "Medical certificate should match the set value");

        MedicalCertificate newCertificate = new MedicalCertificate();
        medicalTraining.setCertificate(newCertificate);
        assertEquals(newCertificate, medicalTraining.getCertificate(), "Medical certificate should update correctly");
    }

    /**
     * Test setting and getting the duration and status.
     */
    @Test
    public void testDurationAndStatus() {
        assertEquals(durationAndStatus, medicalTraining.getDurationAndStatus(), "DurationAndStatus should match the set value");

        DurationAndStatus newDuration = new DurationAndStatus();
        newDuration.setStartDate(java.time.LocalDateTime.of(2024, 6, 1, 8, 0));
        newDuration.setEndDate(java.time.LocalDateTime.of(2024, 12, 31, 18, 0));
        newDuration.setActive((byte) 0);

        medicalTraining.setDurationAndStatus(newDuration);
        assertEquals(newDuration, medicalTraining.getDurationAndStatus(), "DurationAndStatus should update correctly");
    }

    /**
     * Test equality between two MedicalTraining objects.
     */
    @Test
    public void testEquals() {
        MedicalTraining otherTraining = new MedicalTraining();
        otherTraining.setMedicalSchool(medicalSchool);
        otherTraining.setCertificate(medicalCertificate);
        otherTraining.setDurationAndStatus(durationAndStatus);

        assertEquals(medicalTraining, otherTraining, "Two MedicalTraining objects with the same data should be equal");
    }

    /**
     * Test hashCode for consistency between two MedicalTraining objects.
     */
    @Test
    public void testHashCode() {
        MedicalTraining otherTraining = new MedicalTraining();
        otherTraining.setMedicalSchool(medicalSchool);
        otherTraining.setCertificate(medicalCertificate);
        otherTraining.setDurationAndStatus(durationAndStatus);

        assertEquals(medicalTraining.hashCode(), otherTraining.hashCode(), "Hash codes for identical MedicalTraining objects should match");
    }

    /**
     * Test if DurationAndStatus is initialized correctly.
     */
    @Test
    public void testDurationAndStatusInitialization() {
        MedicalTraining newTraining = new MedicalTraining();
        assertNotNull(newTraining.getDurationAndStatus(), "DurationAndStatus should not be null after initialization");
    }
}
