package acmemedical.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the MedicalSchool entity.
 *
 * @author Harmeet Matharoo
 */
public class MedicalSchoolTest {

    private MedicalSchool medicalSchool;
    private MedicalTraining medicalTraining1;
    private MedicalTraining medicalTraining2;

    @BeforeEach
    public void setUp() {
        medicalSchool = new PublicSchool(); // Using a subclass to test the base class behavior
        medicalSchool.setName("Test Medical School");

        medicalTraining1 = new MedicalTraining();
        medicalTraining2 = new MedicalTraining();

        Set<MedicalTraining> medicalTrainings = new HashSet<>();
        medicalTrainings.add(medicalTraining1);
        medicalTrainings.add(medicalTraining2);

        medicalSchool.setMedicalTrainings(medicalTrainings);
    }

    /**
     * Test the default constructor.
     */
    @Test
    public void testDefaultConstructor() {
        MedicalSchool newMedicalSchool = new PublicSchool();
        assertNotNull(newMedicalSchool, "MedicalSchool object should not be null");
    }

    /**
     * Test setting and getting the name.
     */
    @Test
    public void testName() {
        String newName = "Updated Medical School";
        medicalSchool.setName(newName);
        assertEquals(newName, medicalSchool.getName(), "Name should match the set value");
    }

    /**
     * Test setting and getting medical trainings.
     */
    @Test
    public void testMedicalTrainings() {
        Set<MedicalTraining> newMedicalTrainings = new HashSet<>();
        MedicalTraining training = new MedicalTraining();
        newMedicalTrainings.add(training);

        medicalSchool.setMedicalTrainings(newMedicalTrainings);
        assertEquals(newMedicalTrainings, medicalSchool.getMedicalTrainings(), "Medical trainings should match the set value");
    }

    /**
     * Test the inheritance behavior.
     */
    @Test
    public void testInheritance() {
        assertTrue(medicalSchool instanceof MedicalSchool, "PublicSchool should be an instance of MedicalSchool");
    }

    /**
     * Test equals method for equality.
     */
    @Test
    public void testEquals() {
        MedicalSchool otherMedicalSchool = new PublicSchool();
        otherMedicalSchool.setName("Test Medical School");
        assertTrue(medicalSchool.equals(otherMedicalSchool), "Two MedicalSchool objects with the same data should be equal");
    }

    /**
     * Test adding medical trainings to the medical school.
     */
    @Test
    public void testAddMedicalTrainings() {
        Set<MedicalTraining> medicalTrainings = medicalSchool.getMedicalTrainings();
        assertNotNull(medicalTrainings, "Medical trainings should not be null");
        assertEquals(2, medicalTrainings.size(), "Medical school should have 2 medical trainings");
    }
}
