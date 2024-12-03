package acmemedical.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Medicine entity.
 *
 * @author Harmeet Matharoo
 */
public class MedicineTest {

    private Medicine medicine;
    private Prescription prescription1;
    private Prescription prescription2;

    @BeforeEach
    public void setUp() {
        prescription1 = new Prescription();
        prescription2 = new Prescription();

        Set<Prescription> prescriptions = new HashSet<>();
        prescriptions.add(prescription1);
        prescriptions.add(prescription2);

        medicine = new Medicine("TestDrug", "TestManufacturer", "Take one tablet daily", prescriptions);
    }

    /**
     * Test the default constructor.
     */
    @Test
    public void testDefaultConstructor() {
        Medicine newMedicine = new Medicine();
        assertNotNull(newMedicine, "Medicine object should not be null");
    }

    /**
     * Test the parameterized constructor.
     */
    @Test
    public void testParameterizedConstructor() {
        assertEquals("TestDrug", medicine.getDrugName(), "Drug name should match the initialized value");
        assertEquals("TestManufacturer", medicine.getManufacturerName(), "Manufacturer name should match the initialized value");
        assertEquals("Take one tablet daily", medicine.getDosageInformation(), "Dosage information should match the initialized value");
        assertEquals(2, medicine.getPrescriptions().size(), "Prescriptions set should contain the initialized values");
    }

    /**
     * Test setting and getting the drug name.
     */
    @Test
    public void testDrugName() {
        medicine.setDrugName("UpdatedDrug");
        assertEquals("UpdatedDrug", medicine.getDrugName(), "Drug name should match the updated value");
    }

    /**
     * Test setting and getting the manufacturer name.
     */
    @Test
    public void testManufacturerName() {
        medicine.setManufacturerName("UpdatedManufacturer");
        assertEquals("UpdatedManufacturer", medicine.getManufacturerName(), "Manufacturer name should match the updated value");
    }

    /**
     * Test setting and getting the dosage information.
     */
    @Test
    public void testDosageInformation() {
        medicine.setDosageInformation("Updated dosage information");
        assertEquals("Updated dosage information", medicine.getDosageInformation(), "Dosage information should match the updated value");
    }

    /**
     * Test setting and getting the chemical name.
     */
    @Test
    public void testChemicalName() {
        medicine.setChemicalName("TestChemical");
        assertEquals("TestChemical", medicine.getChemicalName(), "Chemical name should match the set value");
    }

    /**
     * Test setting and getting the generic name.
     */
    @Test
    public void testGenericName() {
        medicine.setGenericName("TestGeneric");
        assertEquals("TestGeneric", medicine.getGenericName(), "Generic name should match the set value");
    }

    /**
     * Test setting and getting prescriptions.
     */
    @Test
    public void testPrescriptions() {
        Set<Prescription> newPrescriptions = new HashSet<>();
        Prescription newPrescription = new Prescription();
        newPrescriptions.add(newPrescription);

        medicine.setPrescriptions(newPrescriptions);
        assertEquals(1, medicine.getPrescriptions().size(), "Prescriptions set should match the updated value");
        assertTrue(medicine.getPrescriptions().contains(newPrescription), "Prescriptions set should contain the new prescription");
    }

    /**
     * Test the setMedicine method.
     */
    @Test
    public void testSetMedicine() {
        medicine.setMedicine("NewDrug", "NewManufacturer", "Updated dosage");
        assertEquals("NewDrug", medicine.getDrugName(), "Drug name should match the updated value");
        assertEquals("NewManufacturer", medicine.getManufacturerName(), "Manufacturer name should match the updated value");
        assertEquals("Updated dosage", medicine.getDosageInformation(), "Dosage information should match the updated value");
    }

    /**
     * Test equals method for equality.
     */
    @Test
    public void testEquals() {
        Medicine otherMedicine = new Medicine("TestDrug", "TestManufacturer", "Take one tablet daily", medicine.getPrescriptions());
        assertEquals(medicine, otherMedicine, "Two Medicine objects with the same data should be equal");
    }
}
