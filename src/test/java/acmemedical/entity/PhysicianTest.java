package acmemedical.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Physician entity.
 *
 * @author Harmeet Matharoo
 */
public class PhysicianTest {

    private Physician physician;
    private MedicalCertificate certificate1;
    private MedicalCertificate certificate2;
    private Prescription prescription1;
    private Prescription prescription2;

    @BeforeEach
    public void setUp() {
        physician = new Physician();
        physician.setFirstName("John");
        physician.setLastName("Doe");

        certificate1 = new MedicalCertificate();
        certificate2 = new MedicalCertificate();

        prescription1 = new Prescription();
        prescription2 = new Prescription();

        Set<MedicalCertificate> certificates = new HashSet<>();
        certificates.add(certificate1);
        certificates.add(certificate2);

        Set<Prescription> prescriptions = new HashSet<>();
        prescriptions.add(prescription1);
        prescriptions.add(prescription2);

        physician.setMedicalCertificates(certificates);
        physician.setPrescriptions(prescriptions);
    }

    /**
     * Test setting and getting the first name.
     */
    @Test
    public void testFirstName() {
        physician.setFirstName("Jane");
        assertEquals("Jane", physician.getFirstName(), "First name should match the updated value");
    }

    /**
     * Test setting and getting the last name.
     */
    @Test
    public void testLastName() {
        physician.setLastName("Smith");
        assertEquals("Smith", physician.getLastName(), "Last name should match the updated value");
    }

    /**
     * Test the relationship with medical certificates.
     */
    @Test
    public void testMedicalCertificates() {
        Set<MedicalCertificate> certificates = physician.getMedicalCertificates();
        assertNotNull(certificates, "Medical certificates should not be null");
        assertEquals(2, certificates.size(), "Medical certificates set should contain the correct number of entries");
    }

    /**
     * Test the relationship with prescriptions.
     */
    @Test
    public void testPrescriptions() {
        Set<Prescription> prescriptions = physician.getPrescriptions();
        assertNotNull(prescriptions, "Prescriptions should not be null");
        assertEquals(2, prescriptions.size(), "Prescriptions set should contain the correct number of entries");
    }

    /**
     * Test setting the full name.
     */
    @Test
    public void testSetFullName() {
        physician.setFullName("Alice", "Johnson");
        assertEquals("Alice", physician.getFirstName(), "First name should match the updated value");
        assertEquals("Johnson", physician.getLastName(), "Last name should match the updated value");
    }
}
