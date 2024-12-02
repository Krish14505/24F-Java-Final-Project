package acmemedical.rest.resource;

import static org.junit.jupiter.api.Assertions.*;
import static jakarta.ws.rs.core.Response.Status.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.Medicine;
import acmemedical.entity.Physician;

/**
 * Unit tests for the PhysicianResource class using JUnit without Mockito.
 * 
 * @author Harmeet Matharoo
 */
public class PhysicianResourceTest {

    private PhysicianResource resource;
    private ACMEMedicalServiceStub serviceStub;

    /**
     * Sets up the resource and service stub before each test.
     */
    @BeforeEach
    public void setUp() {
        serviceStub = new ACMEMedicalServiceStub();
        resource = new PhysicianResource();
        resource.service = serviceStub;
    }

    /**
     * Tests the getPhysicians method.
     */
    @Test
    public void testGetPhysicians() {
        Response response = resource.getPhysicians();
        assertEquals(OK.getStatusCode(), response.getStatus());

        @SuppressWarnings("unchecked")
        List<Physician> physicians = (List<Physician>) response.getEntity();
        assertEquals(2, physicians.size());
        assertEquals("John", physicians.get(0).getFirstName());
        assertEquals("Jane", physicians.get(1).getFirstName());
    }

//    /**
//     * Tests the getPhysicianById method.
//     */
//    @Test
//    public void testGetPhysicianById() {
//        Response response = resource.getPhysicianById(1);
//        assertEquals(OK.getStatusCode(), response.getStatus());
//
//        Physician physician = (Physician) response.getEntity();
//        assertEquals("John", physician.getFirstName());
//    }

//    /**
//     * Tests the addPhysician method.
//     */
//    @Test
//    public void testAddPhysician() {
//        Physician newPhysician = new Physician();
//        newPhysician.setFirstName("Alice");
//        newPhysician.setLastName("Brown");
//
//        Response response = resource.addPhysician(newPhysician);
//        assertEquals(OK.getStatusCode(), response.getStatus());
//
//        Physician addedPhysician = (Physician) response.getEntity();
//        assertEquals("Alice", addedPhysician.getFirstName());
//    }

    /**
     * Tests the updateMedicineForPhysicianPatient method.
     */
    @Test
    public void testUpdateMedicineForPhysicianPatient() {
        Medicine newMedicine = new Medicine();
        newMedicine.setDrugName("Aspirin");

        Response response = resource.updateMedicineForPhysicianPatient(1, 2, newMedicine);
        assertEquals(OK.getStatusCode(), response.getStatus());

        Medicine updatedMedicine = (Medicine) response.getEntity();
        assertEquals("Aspirin", updatedMedicine.getDrugName());
    }

    /**
     * Service stub for testing without Mockito.
     */
    private static class ACMEMedicalServiceStub extends ACMEMedicalService {
        private List<Physician> physicians = new ArrayList<>();
        private List<Medicine> medicines = new ArrayList<>();

        public ACMEMedicalServiceStub() {
            Physician physician1 = new Physician();
            physician1.setId(1);
            physician1.setFirstName("John");
            physician1.setLastName("Doe");

            Physician physician2 = new Physician();
            physician2.setId(2);
            physician2.setFirstName("Jane");
            physician2.setLastName("Smith");

            physicians.add(physician1);
            physicians.add(physician2);
        }

        @Override
        public List<Physician> getAllPhysicians() {
            return physicians;
        }

        @Override
        public Physician getPhysicianById(int id) {
            return physicians.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
        }

        @Override
        public Physician persistPhysician(Physician physician) {
            physician.setId(physicians.size() + 1);
            physicians.add(physician);
            return physician;
        }

        @Override
        public Medicine setMedicineForPhysicianPatient(int physicianId, int patientId, Medicine medicine) {
            medicines.add(medicine);
            return medicine;
        }
    }
}
