package acmemedical.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Patient entity.
 *
 * @author Harmeet Matharoo
 */
public class PatientTest {

    private Patient patient;
    private Prescription prescription1;
    private Prescription prescription2;

    @BeforeEach
    public void setUp() {
        prescription1 = new Prescription();
        prescription2 = new Prescription();

        Set<Prescription> prescriptions = new HashSet<>();
        prescriptions.add(prescription1);
        prescriptions.add(prescription2);

        patient = new Patient("John", "Doe", 1985, "123 Elm Street", 180, 75, (byte) 1);
        patient.setPrescription(prescriptions);
    }

    /**
     * Test the default constructor.
     */
    @Test
    public void testDefaultConstructor() {
        Patient newPatient = new Patient();
        assertNotNull(newPatient, "Patient object should not be null");
    }

    /**
     * Test the parameterized constructor.
     */
    @Test
    public void testParameterizedConstructor() {
        assertEquals("John", patient.getFirstName(), "First name should match the initialized value");
        assertEquals("Doe", patient.getLastName(), "Last name should match the initialized value");
        assertEquals(1985, patient.getYear(), "Year should match the initialized value");
        assertEquals("123 Elm Street", patient.getAddress(), "Address should match the initialized value");
        assertEquals(180, patient.getHeight(), "Height should match the initialized value");
        assertEquals(75, patient.getWeight(), "Weight should match the initialized value");
        assertEquals(1, patient.getSmoker(), "Smoker status should match the initialized value");
        assertEquals(2, patient.getPrescriptions().size(), "Prescriptions set should contain the initialized values");
    }

    /**
     * Test setting and getting the first name.
     */
    @Test
    public void testFirstName() {
        patient.setFirstName("Jane");
        assertEquals("Jane", patient.getFirstName(), "First name should match the updated value");
    }

    /**
     * Test setting and getting the last name.
     */
    @Test
    public void testLastName() {
        patient.setLastName("Smith");
        assertEquals("Smith", patient.getLastName(), "Last name should match the updated value");
    }

    /**
     * Test setting and getting the year of birth.
     */
    @Test
    public void testYear() {
        patient.setYear(1990);
        assertEquals(1990, patient.getYear(), "Year should match the updated value");
    }

    /**
     * Test setting and getting the address.
     */
    @Test
    public void testAddress() {
        patient.setAddress("456 Maple Avenue");
        assertEquals("456 Maple Avenue", patient.getAddress(), "Address should match the updated value");
    }

    /**
     * Test setting and getting the height.
     */
    @Test
    public void testHeight() {
        patient.setHeight(170);
        assertEquals(170, patient.getHeight(), "Height should match the updated value");
    }

    /**
     * Test setting and getting the weight.
     */
    @Test
    public void testWeight() {
        patient.setWeight(80);
        assertEquals(80, patient.getWeight(), "Weight should match the updated value");
    }

    /**
     * Test setting and getting the smoker status.
     */
    @Test
    public void testSmoker() {
        patient.setSmoker((byte) 0);
        assertEquals(0, patient.getSmoker(), "Smoker status should match the updated value");
    }

    /**
     * Test setting and getting prescriptions.
     */
    @Test
    public void testPrescriptions() {
        Set<Prescription> newPrescriptions = new HashSet<>();
        Prescription newPrescription = new Prescription();
        newPrescriptions.add(newPrescription);

        patient.setPrescription(newPrescriptions);
        assertEquals(1, patient.getPrescriptions().size(), "Prescriptions set should match the updated value");
        assertTrue(patient.getPrescriptions().contains(newPrescription), "Prescriptions set should contain the new prescription");
    }

    /**
     * Test the setPatient method.
     */
    @Test
    public void testSetPatient() {
        patient.setPatient("Alice", "Johnson", 1992, "789 Oak Street", 165, 68, (byte) 1);
        assertEquals("Alice", patient.getFirstName(), "First name should match the updated value");
        assertEquals("Johnson", patient.getLastName(), "Last name should match the updated value");
        assertEquals(1992, patient.getYear(), "Year should match the updated value");
        assertEquals("789 Oak Street", patient.getAddress(), "Address should match the updated value");
        assertEquals(165, patient.getHeight(), "Height should match the updated value");
        assertEquals(68, patient.getWeight(), "Weight should match the updated value");
        assertEquals(1, patient.getSmoker(), "Smoker status should match the updated value");
    }

    /**
     * Test equals method for equality.
     */
    @Test
    public void testEquals() {
        Patient otherPatient = new Patient("John", "Doe", 1985, "123 Elm Street", 180, 75, (byte) 1);
        otherPatient.setPrescription(patient.getPrescriptions());
        assertEquals(patient, otherPatient, "Two Patient objects with the same data should be equal");
    }
}
