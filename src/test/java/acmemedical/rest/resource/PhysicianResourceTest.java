package acmemedical.rest.resource;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static jakarta.ws.rs.core.Response.Status.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ForbiddenException;

import java.util.ArrayList;
import java.util.List;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.Medicine;
import acmemedical.entity.Physician;
import acmemedical.entity.SecurityUser;

/**
 * Unit tests for the PhysicianResource class.
 * 
 * @author Harmeet Matharoo
 */
public class PhysicianResourceTest {

    @InjectMocks
    private PhysicianResource resource;

    @Mock
    private ACMEMedicalService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test getPhysicians method.
     */
    @Test
    public void testGetPhysicians() {
        List<Physician> mockPhysicians = new ArrayList<>();
        mockPhysicians.add(new Physician() {{ setId(1); setFirstName("John"); setLastName("Doe"); }});
        mockPhysicians.add(new Physician() {{ setId(2); setFirstName("Jane"); setLastName("Smith"); }});

        when(service.getAllPhysicians()).thenReturn(mockPhysicians);

        Response response = resource.getPhysicians();
        assertEquals(OK.getStatusCode(), response.getStatus(), "Response status should be OK");
        assertEquals(mockPhysicians, response.getEntity(), "Response entity should match the mocked physicians list");
    }

    /**
     * Test getPhysicianById method for ADMIN_ROLE.
     */
    @Test
    public void testGetPhysicianById_AdminRole() {
        Physician mockPhysician = new Physician();
        mockPhysician.setId(1);
        mockPhysician.setFirstName("John");
        mockPhysician.setLastName("Doe");

        when(service.getPhysicianById(1)).thenReturn(mockPhysician);

        Response response = resource.getPhysicianById(1);
        assertEquals(OK.getStatusCode(), response.getStatus(), "Response status should be OK");
        assertEquals(mockPhysician, response.getEntity(), "Response entity should match the mocked physician");
    }

    /**
     * Test getPhysicianById method for USER_ROLE when accessing their own physician.
     */
    @Test
    public void testGetPhysicianById_UserRole_OwnPhysician() {
        SecurityUser mockUser = new SecurityUser();
        Physician mockPhysician = new Physician();
        mockPhysician.setId(1);
        mockUser.setPhysician(mockPhysician);

        when(service.getPhysicianById(1)).thenReturn(mockPhysician);

        Response response = resource.getPhysicianById(1);
        assertEquals(OK.getStatusCode(), response.getStatus(), "Response status should be OK");
        assertEquals(mockPhysician, response.getEntity(), "Response entity should match the physician linked to the user");
    }

    /**
     * Test getPhysicianById method for USER_ROLE when accessing a different physician.
     */
    @Test
    public void testGetPhysicianById_UserRole_InvalidAccess() {
        SecurityUser mockUser = new SecurityUser();
        Physician mockPhysician = new Physician();
        mockPhysician.setId(2); // Different ID

        mockUser.setPhysician(mockPhysician);

        assertThrows(ForbiddenException.class, () -> {
            resource.getPhysicianById(1);
        }, "Should throw ForbiddenException for invalid access");
    }

    /**
     * Test addPhysician method.
     */
    @Test
    public void testAddPhysician() {
        Physician newPhysician = new Physician();
        newPhysician.setFirstName("John");
        newPhysician.setLastName("Doe");

        when(service.persistPhysician(newPhysician)).thenReturn(newPhysician);

        Response response = resource.addPhysician(newPhysician);
        assertEquals(OK.getStatusCode(), response.getStatus(), "Response status should be OK");
        assertEquals(newPhysician, response.getEntity(), "Response entity should match the newly added physician");
    }

    /**
     * Test updateMedicineForPhysicianPatient method.
     */
    @Test
    public void testUpdateMedicineForPhysicianPatient() {
        Medicine newMedicine = new Medicine();
        newMedicine.setDrugName("Drug A");

        when(service.setMedicineForPhysicianPatient(1, 2, newMedicine)).thenReturn(newMedicine);

        Response response = resource.updateMedicineForPhysicianPatient(1, 2, newMedicine);
        assertEquals(OK.getStatusCode(), response.getStatus(), "Response status should be OK");
        assertEquals(newMedicine, response.getEntity(), "Response entity should match the updated medicine");
    }
}
