/********************************************************************************************************
 * File:  PatientResourceTest.java
 * Course Materials CST 8277
 *
 * Author: Harmeet Matharoo
 * Date: 2024-12-03
 ********************************************************************************************************/
package acmemedical.rest.resource;

import static acmemedical.utility.MyConstants.PATIENT_RESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.databind.ObjectMapper;

import acmemedical.MyObjectMapperProvider;
import acmemedical.entity.Patient;

/**
 * Integration Tests for PatientResource REST API.
 * 
 * <p>
 * This test suite covers CRUD operations for the Patient entity, including authentication and authorization scenarios.
 * </p>
 * 
 * <p>
 * Author: Harmeet Matharoo
 * Date: 2024-12-03
 * </p>
 */
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PatientResourceTest {
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    private static final Logger logger = LogManager.getLogger(_thisClaz);

    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 8080;

    // Test fixture(s)
    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;

    // List to track created Patient IDs for cleanup
    private List<Integer> createdPatientIds = new ArrayList<>();

    @BeforeAll
    public void oneTimeSetUp() throws Exception {
        logger.debug("oneTimeSetUp");
        uri = UriBuilder
            .fromUri("http://localhost:8080/rest-acmemedical/api/v1")
            .scheme(HTTP_SCHEMA)
            .host(HOST)
            .port(PORT)
            .build();
        adminAuth = HttpAuthenticationFeature.basic("admin", "admin");
        userAuth = HttpAuthenticationFeature.basic("cst8277", "8277");
    }

    protected WebTarget webTarget;
    protected ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        Client client = ClientBuilder.newClient()
                .register(MyObjectMapperProvider.class)
                .register(new LoggingFeature());
        webTarget = client.target(uri);
        objectMapper = new ObjectMapper();
    }

    /**
     * Test Case ID: Patient_TC_01
     * 
     * Test Description: Verify that an admin can retrieve all patients.
     * 
     * Preconditions:
     * - Admin user with username 'admin' and password 'admin' exists.
     * - At least one patient exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 200 OK.
     * - Response body contains a non-empty list of patients.
     */
    @Test
    @Order(1)
    public void test01_getAllPatientsAsAdmin() {
        logger.info("Executing test01_getAllPatientsAsAdmin");

        Response response = webTarget
                .path(PATIENT_RESOURCE_NAME)
                .register(adminAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Assert Status Code
        assertThat(response.getStatus(), is(200));

        // Buffer the entity to allow multiple reads
        response.bufferEntity();

        // Deserialize response
        List<Patient> patients = response.readEntity(new GenericType<List<Patient>>() {});

        // Assert Response Body
        assertThat(patients, is(not(empty())));
        assertThat(patients.size(), is(not(0)));
    }

    /**
     * Test Case ID: Patient_TC_02
     * 
     * Test Description: Verify that a regular user can retrieve all patients.
     * 
     * Preconditions:
     * - User with username 'cst8277' and password '8277' exists.
     * - At least one patient exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 403 Forbidden.
     */
    @Test
    @Order(2)
    public void test02_getAllPatientsAsUser() {
        logger.info("Executing test02_getAllPatientsAsUser");

        Response response = webTarget
                .path(PATIENT_RESOURCE_NAME)
                .register(userAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Assert Status Code (403 Forbidden)
        assertThat(response.getStatus(), is(403));
    }

    /**
     * Test Case ID: Patient_TC_03
     * 
     * Test Description: Verify that an admin can create a new patient.
     * 
     * Preconditions:
     * - Admin user with username 'admin' and password 'admin' exists.
     * - The patient to be created does not already exist.
     * 
     * Expected Result:
     * - HTTP Status 201 Created.
     * - Response body contains the created patient with an assigned ID.
     */
    @Test
    @Order(3)
    public void test03_createNewPatientAsAdmin() {
        logger.info("Executing test03_createNewPatientAsAdmin");

        Patient newPatient = new Patient();
        newPatient.setFirstName("Charles");
        newPatient.setLastName("Xavier");
        newPatient.setYear(1978);
        newPatient.setAddress("456 Main St. Toronto");
        newPatient.setHeight(170);
        newPatient.setWeight(90);
        newPatient.setSmoker((byte)1);

        Response response = webTarget
                .path(PATIENT_RESOURCE_NAME)
                .register(adminAuth)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(newPatient, MediaType.APPLICATION_JSON));

        // Assert Status Code
        assertThat(response.getStatus(), is(201));

        // Buffer the entity to allow multiple reads
        response.bufferEntity();

        // Deserialize response
        Patient createdPatient = response.readEntity(Patient.class);

        // Assert Response Body
        assertNotNull(createdPatient.getId(), "Patient ID should not be null");
        assertEquals("Charles", createdPatient.getFirstName(), "First name should match");
        assertEquals("Xavier", createdPatient.getLastName(), "Last name should match");

        // Track the created Patient ID for cleanup
        createdPatientIds.add(createdPatient.getId());
    }

    /**
     * Test Case ID: Patient_TC_04
     * 
     * Test Description: Verify that a regular user cannot create a new patient.
     * 
     * Preconditions:
     * - User with username 'cst8277' and password '8277' exists.
     * 
     * Expected Result:
     * - HTTP Status 403 Forbidden.
     */
    @Test
    @Order(4)
    public void test04_createNewPatientAsUserForbidden() {
        logger.info("Executing test04_createNewPatientAsUserForbidden");

        Patient newPatient = new Patient();
        newPatient.setFirstName("Erik");
        newPatient.setLastName("Lensherr");
        newPatient.setYear(1969);
        newPatient.setAddress("789 Elm St. New York");
        newPatient.setHeight(175);
        newPatient.setWeight(80);
        newPatient.setSmoker((byte)0);

        Response response = webTarget
                .path(PATIENT_RESOURCE_NAME)
                .register(userAuth)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(newPatient, MediaType.APPLICATION_JSON));

        // Assert Status Code
        assertThat(response.getStatus(), is(403));
    }

    /**
     * Test Case ID: Patient_TC_05
     * 
     * Test Description: Verify that an admin can retrieve a patient by ID.
     * 
     * Preconditions:
     * - Admin user with username 'admin' and password 'admin' exists.
     * - A patient with the specified ID exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 200 OK.
     * - Response body contains the patient details.
     */
    @Test
    @Order(5)
    public void test05_getPatientByIdAsAdmin() {
        logger.info("Executing test05_getPatientByIdAsAdmin");

        int patientId = 1; // Adjust based on your test data

        Response response = webTarget
                .path(PATIENT_RESOURCE_NAME + "/" + patientId)
                .register(adminAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Assert Status Code
        assertThat(response.getStatus(), is(200));

        // Deserialize response
        Patient patient = response.readEntity(Patient.class);

        // Assert Response Body
        assertNotNull(patient, "Patient should not be null");
        assertEquals(patientId, patient.getId(), "Patient ID should match");
    }

    /**
     * Test Case ID: Patient_TC_06
     * 
     * Test Description: Verify that a regular user can retrieve their own patient details by ID.
     * 
     * Preconditions:
     * - User with username 'cst8277' and password '8277' exists.
     * - The user is associated with a patient with ID 1.
     * 
     * Expected Result:
     * - HTTP Status 200 OK.
     * - Response body contains the patient details.
     */
    @Test
    @Order(6)
    public void test06_getPatientByIdAsUser() {
        logger.info("Executing test06_getPatientByIdAsUser");

        int patientId = 1; // Adjust based on your test data

        Response response = webTarget
                .path(PATIENT_RESOURCE_NAME + "/" + patientId)
                .register(userAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Assert Status Code
        assertThat(response.getStatus(), is(200)); // Adjust if user role has access

        // Deserialize response
        Patient patient = response.readEntity(Patient.class);

        // Assert Response Body
        assertNotNull(patient, "Patient should not be null");
        assertEquals(patientId, patient.getId(), "Patient ID should match");
    }

    /**
     * Test Case ID: Patient_TC_07
     * 
     * Test Description: Verify that an admin can update a patient's details.
     * 
     * Preconditions:
     * - Admin user with username 'admin' and password 'admin' exists.
     * - A patient with the specified ID exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 200 OK.
     * - Response body contains the updated patient details.
     */
    @Test
    @Order(7)
    public void test07_updatePatientAsAdmin() {
        logger.info("Executing test07_updatePatientAsAdmin");

        int patientId = 1; // Adjust based on your test data
        Patient updatedPatient = new Patient();
        updatedPatient.setFirstName("Charles");
        updatedPatient.setLastName("Xaviers");
        updatedPatient.setYear(1178); // Invalid year for testing
        updatedPatient.setAddress("456 Main St. Toronto");
        updatedPatient.setHeight(170);
        updatedPatient.setWeight(90);
        updatedPatient.setSmoker((byte)1);

        Response response = webTarget
                .path(PATIENT_RESOURCE_NAME + "/" + patientId)
                .register(adminAuth)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(updatedPatient, MediaType.APPLICATION_JSON));

        // Assert Status Code
        assertThat(response.getStatus(), is(200));

        // Deserialize response
        Patient patient = response.readEntity(Patient.class);

        // Assert Response Body
        assertNotNull(patient, "Patient should not be null");
        assertEquals(patientId, patient.getId(), "Patient ID should match");
        assertEquals("Charles", patient.getFirstName(), "First name should be updated");
        assertEquals("Xaviers", patient.getLastName(), "Last name should be updated");
        assertEquals(1178, patient.getYear(), "Year should be updated"); // Adjust based on validation
    }

    /**
     * Test Case ID: Patient_TC_08
     * 
     * Test Description: Verify that a regular user cannot update a patient's details.
     * 
     * Preconditions:
     * - User with username 'cst8277' and password '8277' exists.
     * - A patient with the specified ID exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 403 Forbidden.
     */
    @Test
    @Order(8)
    public void test08_updatePatientAsUserForbidden() {
        logger.info("Executing test08_updatePatientAsUserForbidden");

        int patientId = 1; // Adjust based on your test data
        Patient updatedPatient = new Patient();
        updatedPatient.setFirstName("Erik");
        updatedPatient.setLastName("Lehnsherr");
        updatedPatient.setYear(1960);
        updatedPatient.setAddress("789 Maple St. Boston");
        updatedPatient.setHeight(180);
        updatedPatient.setWeight(85);
        updatedPatient.setSmoker((byte)0);

        Response response = webTarget
                .path(PATIENT_RESOURCE_NAME + "/" + patientId)
                .register(userAuth)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(updatedPatient, MediaType.APPLICATION_JSON));

        // Assert Status Code
        assertThat(response.getStatus(), is(403));
    }

    /**
     * Test Case ID: Patient_TC_09
     * 
     * Test Description: Verify that an admin can delete a patient by ID.
     * 
     * Preconditions:
     * - Admin user with username 'admin' and password 'admin' exists.
     * - A patient with the specified ID exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 204 No Content.
     * - Patient is removed from the database.
     */
    @Test
    @Order(9)
    public void test09_deletePatientAsAdmin() {
        logger.info("Executing test09_deletePatientAsAdmin");
        
        int patientId = 3; // Adjust based on your test data

        Response response = webTarget
                .path(PATIENT_RESOURCE_NAME + "/" + patientId)
                .register(adminAuth)
                .request()
                .delete();

        // Assert Status Code
        assertThat(response.getStatus(), is(204));

        // Verify Deletion by attempting to retrieve the deleted patient
        Response getResponse = webTarget
                .path(PATIENT_RESOURCE_NAME + "/" + patientId)
                .register(adminAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertThat(getResponse.getStatus(), is(404));
    }

    /**
     * Test Case ID: Patient_TC_10
     * 
     * Test Description: Verify that a regular user cannot delete a patient by ID.
     * 
     * Preconditions:
     * - User with username 'cst8277' and password '8277' exists.
     * - A patient with the specified ID exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 403 Forbidden.
     */
    @Test
    @Order(10)
    public void test10_deletePatientAsUserForbidden() {
        logger.info("Executing test10_deletePatientAsUserForbidden");

        int patientId = 1; // Adjust based on your test data

        Response response = webTarget
                .path(PATIENT_RESOURCE_NAME + "/" + patientId)
                .register(userAuth)
                .request()
                .delete();

        // Assert Status Code
        assertThat(response.getStatus(), is(403));
    }

    /**
     * Test Case ID: Patient_TC_11
     * 
     * Test Description: Verify that deleting a non-existent patient returns 404 Not Found.
     * 
     * Preconditions:
     * - Admin user with username 'admin' and password 'admin' exists.
     * - No patient exists with the specified ID.
     * 
     * Expected Result:
     * - HTTP Status 404 Not Found.
     */
    @Test
    @Order(11)
    public void test11_deleteNonExistentPatientAsAdmin() {
        logger.info("Executing test11_deleteNonExistentPatientAsAdmin");

        int patientId = 9999; // Assume this ID does not exist

        Response response = webTarget
                .path(PATIENT_RESOURCE_NAME + "/" + patientId)
                .register(adminAuth)
                .request()
                .delete();

        // Assert Status Code
        assertThat(response.getStatus(), is(404));
    }

    /**
     * Test Case ID: Patient_TC_12
     * 
     * Test Description: Verify that an admin can retrieve a non-existent patient by ID.
     * 
     * Preconditions:
     * - Admin user with username 'admin' and password 'admin' exists.
     * - No patient exists with the specified ID.
     * 
     * Expected Result:
     * - HTTP Status 404 Not Found.
     */
    @Test
    @Order(12)
    public void test12_getNonExistentPatientByIdAsAdmin() {
        logger.info("Executing test12_getNonExistentPatientByIdAsAdmin");

        int patientId = 9999; // Assume this ID does not exist

        Response response = webTarget
                .path(PATIENT_RESOURCE_NAME + "/" + patientId)
                .register(adminAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Assert Status Code
        assertThat(response.getStatus(), is(404));
    }

    /**
     * Test Case ID: Patient_TC_16
     * 
     * Test Description: Verify that deleting a non-existent patient returns 404 Not Found.
     * 
     * Preconditions:
     * - Admin user with username 'admin' and password 'admin' exists.
     * - No patient exists with the specified ID.
     * 
     * Expected Result:
     * - HTTP Status 404 Not Found.
     */
    @Test
    @Order(16)
    public void test16_deleteNonExistentPatientAsAdmin() {
        logger.info("Executing test16_deleteNonExistentPatientAsAdmin");

        int patientId = 8888; // Assume this ID does not exist

        Response response = webTarget
                .path(PATIENT_RESOURCE_NAME + "/" + patientId)
                .register(adminAuth)
                .request()
                .delete();

        // Assert Status Code
        assertThat(response.getStatus(), is(404));
    }

    /**
     * Test Case ID: Patient_TC_17
     * 
     * Test Description: Verify that updating a non-existent patient returns 404 Not Found.
     * 
     * Preconditions:
     * - Admin user with username 'admin' and password 'admin' exists.
     * - No patient exists with the specified ID.
     * 
     * Expected Result:
     * - HTTP Status 404 Not Found.
     */
    @Test
    @Order(17)
    public void test17_updateNonExistentPatientAsAdmin() {
        logger.info("Executing test17_updateNonExistentPatientAsAdmin");

        int patientId = 7777; // Assume this ID does not exist
        Patient updatedPatient = new Patient();
        updatedPatient.setFirstName("Logan");
        updatedPatient.setLastName("Howlett");
        updatedPatient.setYear(1970);
        updatedPatient.setAddress("1010 Birch St. Seattle");
        updatedPatient.setHeight(180);
        updatedPatient.setWeight(85);
        updatedPatient.setSmoker((byte)0);

        Response response = webTarget
                .path(PATIENT_RESOURCE_NAME + "/" + patientId)
                .register(adminAuth)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(updatedPatient, MediaType.APPLICATION_JSON));

        // Assert Status Code
        assertThat(response.getStatus(), is(404));
    }

    /**
     * Test Case ID: Patient_TC_18
     * 
     * Test Description: Verify that accessing the PatientResource without authentication returns 401 Unauthorized.
     * 
     * Preconditions:
     * - No authentication credentials are provided.
     * 
     * Expected Result:
     * - HTTP Status 401 Unauthorized.
     */
    @Test
    @Order(18)
    public void test18_accessPatientResourceWithoutAuth() {
        logger.info("Executing test18_accessPatientResourceWithoutAuth");

        Response response = webTarget
                .path(PATIENT_RESOURCE_NAME)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Assert Status Code
        assertThat(response.getStatus(), is(401));
    }

    /**
     * Test Case ID: Patient_TC_19
     * 
     * Test Description: Verify that providing invalid credentials results in 401 Unauthorized.
     * 
     * Preconditions:
     * - Invalid user credentials are used.
     * 
     * Expected Result:
     * - HTTP Status 401 Unauthorized.
     */
    @Test
    @Order(19)
    public void test19_accessPatientResourceWithInvalidAuth() {
        logger.info("Executing test19_accessPatientResourceWithInvalidAuth");

        HttpAuthenticationFeature invalidAuth = HttpAuthenticationFeature.basic("invalidUser", "invalidPass");

        Response response = webTarget
                .path(PATIENT_RESOURCE_NAME)
                .register(invalidAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Assert Status Code
        assertThat(response.getStatus(), is(401));
    }

    /**
     * Test Case ID: Patient_TC_20
     * 
     * Test Description: Verify that the PatientResource returns JSON content type.
     * 
     * Preconditions:
     * - Admin user with username 'admin' and password 'admin' exists.
     * 
     * Expected Result:
     * - HTTP Status 200 OK.
     * - Response Content-Type is application/json.
     */
    @Test
    @Order(20)
    public void test20_patientResourceContentType() {
        logger.info("Executing test20_patientResourceContentType");

        Response response = webTarget
                .path(PATIENT_RESOURCE_NAME)
                .register(adminAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Assert Status Code
        assertThat(response.getStatus(), is(200));

        // Assert Content-Type
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString("Content-Type"));
    }

    /**
     * Cleanup method to remove any Patient entities created during tests.
     */
    @AfterAll
    public void cleanup() {
        logger.info("Executing cleanup after all tests");

        for (Integer patientId : createdPatientIds) {
            logger.debug("Deleting Patient with ID: {}", patientId);
            Response response = webTarget
                    .path(PATIENT_RESOURCE_NAME + "/" + patientId)
                    .register(adminAuth)
                    .request()
                    .delete();

            if (response.getStatus() == 204 || response.getStatus() == 200) {
                logger.debug("Successfully deleted Patient with ID: {}", patientId);
            } else {
                logger.warn("Failed to delete Patient with ID: {}. Status: {}", patientId, response.getStatus());
            }
        }
    }

    // Continue adding tests following the same pattern for the remaining test cases...

    // Example Placeholder for Additional Tests:
    // @Test
    // @Order(21)
    // public void test21_someOtherTest() {
    //     // Implement test
    // }
}
