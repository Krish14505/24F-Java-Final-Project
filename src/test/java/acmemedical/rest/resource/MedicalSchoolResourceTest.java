
package acmemedical.rest.resource;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.Response;

import java.util.List;

import acmemedical.entity.MedicalSchool;

/**
 * Test class for the MedicalSchoolResource.
 * This class contains unit tests to validate the behavior of the MedicalSchoolResource methods.
 *
 * @author Harmeet Matharoo
 */
public class MedicalSchoolResourceTest {

    private MedicalSchoolResource resource;

    /**
     * Initializes the MedicalSchoolResource and its dependencies before each test.
     */
    @BeforeEach
    public void setUp() {
        resource = new MedicalSchoolResource();
        resource.service = new ACMEMedicalServiceStub();
    }

    /**
     * Tests the retrieval of all medical schools.
     * Verifies that the response status is OK and the list of schools is as expected.
     */
    @Test
    public void testGetMedicalSchools() {
        Response response = resource.getMedicalSchools();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        @SuppressWarnings("unchecked")
        List<MedicalSchool> schools = (List<MedicalSchool>) response.getEntity();
        assertEquals(2, schools.size());
        assertEquals("Public Medical School", schools.get(0).getName());
        assertEquals("Private Medical School", schools.get(1).getName());
    }

    /**
     * Tests the retrieval of a specific medical school by its ID.
     * Verifies that the response status is OK and the retrieved school matches the expected data.
     */
    @Test
    public void testGetMedicalSchoolById() {
        Response response = resource.getMedicalSchoolById(1);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        MedicalSchool school = (MedicalSchool) response.getEntity();
        assertEquals("Public Medical School", school.getName());
    }
}
