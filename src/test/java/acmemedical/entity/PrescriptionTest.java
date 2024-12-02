package acmemedical.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Prescription entity.
 * 
 * @author Harmeet Matharoo
 */
public class PrescriptionTest {

    private Prescription prescription;
    private Physician physician;
    private Patient patient;
    private Medicine medicine;

    @BeforeEach
    public void setUp() {
        physician = new Physician();
        physician.setId(1);
        physician.setFirstName("John");
        physician.setLastName("Doe");

        patient = new Patient();
        patient.setId(2);
        patient.setFirstName("Jane");
        patient.setLastName("Smith");

        medicine = new Medicine();
        medicine.setId(3);
        medicine.setDrugName("Aspirin");
        medicine.setManufacturerName("Pharma Inc.");
        medicine.setDosageInformation("Take one tablet daily");

        prescription = new Prescription();
        prescription.setPhysician(physician);
        prescription.setPatient(patient);
        prescription.setMedicine(medicine);
        prescription.setNumberOfRefills(3);
        prescription.setPrescriptionInformation("For headache relief");
    }

    /**
     * Test the default constructor.
     */
    @Test
    public void testDefaultConstructor() {
        Prescription newPrescription = new Prescription();
        assertNotNull(newPrescription, "Prescription object should not be null");
        assertNotNull(newPrescription.getId(), "Embedded ID should not be null in the default constructor");
    }

    /**
     * Test setting and getting the physician.
     */
    @Test
    public void testPhysician() {
        assertEquals(physician, prescription.getPhysician(), "Physician should match the set value");

        Physician newPhysician = new Physician();
        newPhysician.setId(4);
        prescription.setPhysician(newPhysician);
        assertEquals(newPhysician, prescription.getPhysician(), "Physician should update correctly");
    }

    /**
     * Test setting and getting the patient.
     */
    @Test
    public void testPatient() {
        assertEquals(patient, prescription.getPatient(), "Patient should match the set value");

        Patient newPatient = new Patient();
        newPatient.setId(5);
        prescription.setPatient(newPatient);
        assertEquals(newPatient, prescription.getPatient(), "Patient should update correctly");
    }

    /**
     * Test setting and getting the medicine.
     */
    @Test
    public void testMedicine() {
        assertEquals(medicine, prescription.getMedicine(), "Medicine should match the set value");

        Medicine newMedicine = new Medicine();
        newMedicine.setId(6);
        prescription.setMedicine(newMedicine);
        assertEquals(newMedicine, prescription.getMedicine(), "Medicine should update correctly");
    }

    /**
     * Test setting and getting the number of refills.
     */
    @Test
    public void testNumberOfRefills() {
        prescription.setNumberOfRefills(5);
        assertEquals(5, prescription.getNumberOfRefills(), "Number of refills should match the updated value");
    }

    /**
     * Test setting and getting the prescription information.
     */
    @Test
    public void testPrescriptionInformation() {
        prescription.setPrescriptionInformation("Updated information");
        assertEquals("Updated information", prescription.getPrescriptionInformation(), "Prescription information should match the updated value");
    }

    /**
     * Test the composite key setup through the relationship fields.
     */
    @Test
    public void testCompositeKeySetup() {
        assertEquals(physician.getId(), prescription.getId().getPhysicianId(), "Physician ID in composite key should match the physician's ID");
        assertEquals(patient.getId(), prescription.getId().getPatientId(), "Patient ID in composite key should match the patient's ID");
    }
}
