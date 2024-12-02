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

import java.util.ArrayList;
import java.util.List;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.MedicalSchool;
import acmemedical.entity.MedicalTraining;

/**
 * Unit tests for the MedicalSchoolResource class.
 * 
 * @author Harmeet Matharoo
 */
public class MedicalSchoolResourceTest {

    @InjectMocks
    private MedicalSchoolResource resource;

    @Mock
    private ACMEMedicalService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test getMedicalSchools method.
     */
    @Test
    public void testGetMedicalSchools() {
        List<MedicalSchool> mockSchools = new ArrayList<>();
        mockSchools.add(new MedicalSchool() {{ setId(1); setName("School A"); }});
        mockSchools.add(new MedicalSchool() {{ setId(2); setName("School B"); }});

        when(service.getAllMedicalSchools()).thenReturn(mockSchools);

        Response response = resource.getMedicalSchools();
        assertEquals(OK.getStatusCode(), response.getStatus(), "Response status should be OK");
        assertEquals(mockSchools, response.getEntity(), "Response entity should match the mocked medical schools");
    }

    /**
     * Test getMedicalSchoolById method.
     */
    @Test
    public void testGetMedicalSchoolById() {
        MedicalSchool mockSchool = new MedicalSchool();
        mockSchool.setId(1);
        mockSchool.setName("School A");

        when(service.getMedicalSchoolById(1)).thenReturn(mockSchool);

        Response response = resource.getMedicalSchoolById(1);
        assertEquals(OK.getStatusCode(), response.getStatus(), "Response status should be OK");
        assertEquals(mockSchool, response.getEntity(), "Response entity should match the mocked medical school");
    }

    /**
     * Test deleteMedicalSchool method.
     */
    @Test
    public void testDeleteMedicalSchool() {
        MedicalSchool mockSchool = new MedicalSchool();
        mockSchool.setId(1);
        mockSchool.setName("School A");

        when(service.deleteMedicalSchool(1)).thenReturn(mockSchool);

        Response response = resource.deleteMedicalSchool(1);
        assertEquals(OK.getStatusCode(), response.getStatus(), "Response status should be OK");
        assertEquals(mockSchool, response.getEntity(), "Response entity should match the deleted medical school");
    }

    /**
     * Test addMedicalSchool method when the school is not a duplicate.
     */
    @Test
    public void testAddMedicalSchool_NotDuplicate() {
        MedicalSchool newSchool = new MedicalSchool();
        newSchool.setName("School A");

        when(service.isDuplicated(newSchool)).thenReturn(false);
        when(service.persistMedicalSchool(newSchool)).thenReturn(newSchool);

        Response response = resource.addMedicalSchool(newSchool);
        assertEquals(OK.getStatusCode(), response.getStatus(), "Response status should be OK");
        assertEquals(newSchool, response.getEntity(), "Response entity should match the newly added medical school");
    }

    /**
     * Test addMedicalSchool method when the school is a duplicate.
     */
    @Test
    public void testAddMedicalSchool_Duplicate() {
        MedicalSchool newSchool = new MedicalSchool();
        newSchool.setName("School A");

        when(service.isDuplicated(newSchool)).thenReturn(true);

        Response response = resource.addMedicalSchool(newSchool);
        assertEquals(CONFLICT.getStatusCode(), response.getStatus(), "Response status should be CONFLICT");
        assertTrue(response.getEntity() instanceof HttpErrorResponse, "Response entity should be an instance of HttpErrorResponse");
    }

    /**
     * Test updateMedicalSchool method.
     */
    @Test
    public void testUpdateMedicalSchool() {
        MedicalSchool updatedSchool = new MedicalSchool();
        updatedSchool.setId(1);
        updatedSchool.setName("Updated School A");

        when(service.updateMedicalSchool(1, updatedSchool)).thenReturn(updatedSchool);

        Response response = resource.updateMedicalSchool(1, updatedSchool);
        assertEquals(OK.getStatusCode(), response.getStatus(), "Response status should be OK");
        assertEquals(updatedSchool, response.getEntity(), "Response entity should match the updated medical school");
    }

    /**
     * Test addMedicalTrainingToMedicalSchool method.
     */
    @Test
    public void testAddMedicalTrainingToMedicalSchool() {
        MedicalSchool mockSchool = new MedicalSchool();
        mockSchool.setId(1);

        MedicalTraining newTraining = new MedicalTraining();

        when(service.getMedicalSchoolById(1)).thenReturn(mockSchool);

        Response response = resource.addMedicalTrainingToMedicalSchool(1, newTraining);
        verify(service).updateMedicalSchool(eq(1), any(MedicalSchool.class));
        assertEquals(OK.getStatusCode(), response.getStatus(), "Response status should be OK");
    }
}
