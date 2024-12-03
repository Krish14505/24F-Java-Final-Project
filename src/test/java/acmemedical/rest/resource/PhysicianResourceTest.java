/********************************************************************************************************
 * File:  PhysicianResourceTest.java
 * Course Materials CST 8277
 *
 * @author 
 *         Harmeet Matharoo
 * @date 2024-04-27
 */
package acmemedical.rest.resource;

import static acmemedical.utility.MyConstants.PHYSICIAN_RESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.invoke.MethodHandles;
import java.net.URI;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.databind.ObjectMapper;

import acmemedical.MyObjectMapperProvider;
import acmemedical.entity.Physician;

/**
 * Integration Tests for PhysicianResource REST API.
 * 
 * <p>
 * This test suite covers CRUD operations for the Physician entity, including authentication and authorization scenarios.
 * </p>
 * 
 * <p>
 * Author: Harmeet Matharoo
 * Date: 2024-04-27
 * </p>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PhysicianResourceTest {
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    private static final Logger logger = LogManager.getLogger(_thisClaz);

    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 8080;

    // Test fixture(s)
    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;

    @BeforeAll
    public static void oneTimeSetUp() throws Exception {
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
     * Test Case ID: Physician_TC_01
     * 
     * Test Description: Verify that an admin can retrieve all physicians.
     * 
     * Preconditions:
     * - Admin user with username 'admin' and password 'admin' exists.
     * - At least one physician exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 200 OK.
     * - Response body contains a non-empty list of physicians.
     */
    @Test
    @Order(1)
    public void test01_getAllPhysiciansAsAdmin() {
        logger.info("Executing test01_getAllPhysiciansAsAdmin");

        Response response = webTarget
                .path(PHYSICIAN_RESOURCE_NAME)
                .register(adminAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Assert Status Code
        assertThat(response.getStatus(), is(200));

        // Buffer the entity to allow multiple reads
        response.bufferEntity();

        // Deserialize response
        List<Physician> physicians = response.readEntity(new GenericType<List<Physician>>() {});

        // Assert Response Body
        assertThat(physicians, is(not(empty())));
        assertThat(physicians.size(), is(not(0)));
    }

    /**
     * Test Case ID: Physician_TC_02
     * 
     * Test Description: Verify that a regular user can retrieve all physicians.
     * 
     * Preconditions:
     * - User with username 'cst8277' and password '8277' exists.
     * - At least one physician exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 200 OK.
     * - Response body contains a list of physicians.
     */
    @Test
    @Order(2)
    public void test02_getAllPhysiciansAsUser() {
        logger.info("Executing test02_getAllPhysiciansAsUser");

        Response response = webTarget
                .path(PHYSICIAN_RESOURCE_NAME)
                .register(userAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Assert Status Code
        assertThat(response.getStatus(), is(403));
    }

    /**
     * Test Case ID: Physician_TC_03
     * 
     * Test Description: Verify that an admin can create a new physician.
     * 
     * Preconditions:
     * - Admin user with username 'admin' and password 'admin' exists.
     * - The physician to be created does not already exist.
     * 
     * Expected Result:
     * - HTTP Status 201 Created.
     * - Response body contains the created physician with an assigned ID.
     */
    @Test
    @Order(3)
    public void test03_createNewPhysicianAsAdmin() {
        logger.info("Executing test03_createNewPhysicianAsAdmin");

        Physician newPhysician = new Physician();
        newPhysician.setFirstName("Michael");
        newPhysician.setLastName("Jordan");

        Response response = webTarget
                .path(PHYSICIAN_RESOURCE_NAME)
                .register(adminAuth)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(newPhysician, MediaType.APPLICATION_JSON));

        // Assert Status Code
        assertThat(response.getStatus(), is(201));

        // Buffer the entity to allow multiple reads
        response.bufferEntity();

        // Deserialize response
        Physician createdPhysician = response.readEntity(Physician.class);

        // Assert Response Body
        assertNotNull(createdPhysician.getId(), "Physician ID should not be null");
        assertEquals("Michael", createdPhysician.getFirstName(), "First name should match");
        assertEquals("Jordan", createdPhysician.getLastName(), "Last name should match");
    }

    /**
     * Test Case ID: Physician_TC_04
     * 
     * Test Description: Verify that a regular user cannot create a new physician.
     * 
     * Preconditions:
     * - User with username 'cst8277' and password '8277' exists.
     * 
     * Expected Result:
     * - HTTP Status 403 Forbidden.
     */
    @Test
    @Order(4)
    public void test04_createNewPhysicianAsUserForbidden() {
        logger.info("Executing test04_createNewPhysicianAsUserForbidden");

        Physician newPhysician = new Physician();
        newPhysician.setFirstName("LeBron");
        newPhysician.setLastName("James");

        Response response = webTarget
                .path(PHYSICIAN_RESOURCE_NAME)
                .register(userAuth)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(newPhysician, MediaType.APPLICATION_JSON));

        // Assert Status Code
        assertThat(response.getStatus(), is(403));
    }

    /**
     * Test Case ID: Physician_TC_05
     * 
     * Test Description: Verify that an admin can retrieve a physician by ID.
     * 
     * Preconditions:
     * - Admin user with username 'admin' and password 'admin' exists.
     * - A physician with the specified ID exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 200 OK.
     * - Response body contains the physician details.
     */
    @Test
    @Order(5)
    public void test05_getPhysicianByIdAsAdmin() {
        logger.info("Executing test05_getPhysicianByIdAsAdmin");

        int physicianId = 1; // Adjust based on your test data

        Response response = webTarget
                .path(PHYSICIAN_RESOURCE_NAME + "/" + physicianId)
                .register(adminAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Assert Status Code
        assertThat(response.getStatus(), is(200));

        // Deserialize response
        Physician physician = response.readEntity(Physician.class);

        // Assert Response Body
        assertNotNull(physician, "Physician should not be null");
        assertEquals(physicianId, physician.getId(), "Physician ID should match");
    }

    /**
     * Test Case ID: Physician_TC_06
     * 
     * Test Description: Verify that a regular user can retrieve their own physician details by ID.
     * 
     * Preconditions:
     * - User with username 'cst8277' and password '8277' exists.
     * - The user is associated with a physician with ID 1.
     * 
     * Expected Result:
     * - HTTP Status 200 OK.
     * - Response body contains the physician details.
     */
    @Test
    @Order(6)
    public void test06_getPhysicianByIdAsUser() {
        logger.info("Executing test06_getPhysicianByIdAsUser");

        int physicianId = 1; // Adjust based on your test data

        Response response = webTarget
                .path(PHYSICIAN_RESOURCE_NAME + "/" + physicianId)
                .register(userAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Assert Status Code
        assertThat(response.getStatus(), is(200));

        // Deserialize response
        Physician physician = response.readEntity(Physician.class);

        // Assert Response Body
        assertNotNull(physician, "Physician should not be null");
        assertEquals(physicianId, physician.getId(), "Physician ID should match");
    }

    /**
     * Test Case ID: Physician_TC_07
     * 
     * Test Description: Verify that a regular user cannot retrieve another physician's details.
     * 
     * Preconditions:
     * - User with username 'cst8277' and password '8277' exists.
     * - A physician with a different ID exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 403 Forbidden.
     */
    @Test
    @Order(7)
    public void test07_getPhysicianByIdAsUserForbidden() {
        logger.info("Executing test07_getPhysicianByIdAsUserForbidden");

        int physicianId = 2; // Adjust to an ID not associated with 'cst8277'

        Response response = webTarget
                .path(PHYSICIAN_RESOURCE_NAME + "/" + physicianId)
                .register(userAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Assert Status Code
        assertThat(response.getStatus(), is(403));
    }

    /**
     * Test Case ID: Physician_TC_08
     * 
     * Test Description: Verify that an admin can update a physician's details.
     * 
     * Preconditions:
     * - Admin user with username 'admin' and password 'admin' exists.
     * - A physician with the specified ID exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 200 OK.
     * - Response body contains the updated physician details.
     */
    @Test
    @Order(8)
    public void test08_updatePhysicianAsAdmin() {
        logger.info("Executing test08_updatePhysicianAsAdmin");

        int physicianId = 1; // Adjust based on your test data
        Physician updatedPhysician = new Physician();
        updatedPhysician.setFirstName("Michael");
        updatedPhysician.setLastName("Jordan");

        Response response = webTarget
                .path(PHYSICIAN_RESOURCE_NAME + "/" + physicianId)
                .register(adminAuth)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(updatedPhysician, MediaType.APPLICATION_JSON));

        // Assert Status Code
        assertThat(response.getStatus(), is(200));

        // Deserialize response
        Physician physician = response.readEntity(Physician.class);

        // Assert Response Body
        assertNotNull(physician, "Physician should not be null");
        assertEquals(physicianId, physician.getId(), "Physician ID should match");
        assertEquals("John", physician.getFirstName(), "First name should be updated");
        assertEquals("Smith", physician.getLastName(), "Last name should be updated");
    }

    /**
     * Test Case ID: Physician_TC_09
     * 
     * Test Description: Verify that a regular user cannot update a physician's details.
     * 
     * Preconditions:
     * - User with username 'cst8277' and password '8277' exists.
     * - A physician with the specified ID exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 403 Forbidden.
     */
    @Test
    @Order(9)
    public void test09_updatePhysicianAsUserForbidden() {
        logger.info("Executing test09_updatePhysicianAsUserForbidden");

        int physicianId = 1; // Adjust based on your test data
        Physician updatedPhysician = new Physician();
        updatedPhysician.setFirstName("LeBron");
        updatedPhysician.setLastName("James");

        Response response = webTarget
                .path(PHYSICIAN_RESOURCE_NAME + "/" + physicianId)
                .register(userAuth)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(updatedPhysician, MediaType.APPLICATION_JSON));

        // Assert Status Code
        assertThat(response.getStatus(), is(403));
    }

    /**
     * Test Case ID: Physician_TC_10
     * 
     * Test Description: Verify that an admin can delete a physician by ID.
     * 
     * Preconditions:
     * - Admin user with username 'admin' and password 'admin' exists.
     * - A physician with the specified ID exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 204 No Content.
     * - Physician is removed from the database.
     */
    @Test
    @Order(10)
    public void test10_deletePhysicianAsAdmin() {
        logger.info("Executing test10_deletePhysicianAsAdmin");

        int physicianId = 2; // Adjust based on your test data

        Response response = webTarget
                .path(PHYSICIAN_RESOURCE_NAME + "/" + physicianId)
                .register(adminAuth)
                .request()
                .delete();

        // Assert Status Code
        assertThat(response.getStatus(), is(204));

        // Verify Deletion by attempting to retrieve the deleted physician
        Response getResponse = webTarget
                .path(PHYSICIAN_RESOURCE_NAME + "/" + physicianId)
                .register(adminAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertThat(getResponse.getStatus(), is(404));
    }

    /**
     * Test Case ID: Physician_TC_11
     * 
     * Test Description: Verify that a regular user cannot delete a physician by ID.
     * 
     * Preconditions:
     * - User with username 'cst8277' and password '8277' exists.
     * - A physician with the specified ID exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 403 Forbidden.
     */
    @Test
    @Order(11)
    public void test11_deletePhysicianAsUserForbidden() {
        logger.info("Executing test11_deletePhysicianAsUserForbidden");

        int physicianId = 1; // Adjust based on your test data

        Response response = webTarget
                .path(PHYSICIAN_RESOURCE_NAME + "/" + physicianId)
                .register(userAuth)
                .request()
                .delete();

        // Assert Status Code
        assertThat(response.getStatus(), is(403));
    }

    /**
     * Test Case ID: Physician_TC_12
     * 
     * Test Description: Verify that an admin can retrieve a non-existent physician by ID.
     * 
     * Preconditions:
     * - Admin user with username 'admin' and password 'admin' exists.
     * - No physician exists with the specified ID.
     * 
     * Expected Result:
     * - HTTP Status 404 Not Found.
     */
    @Test
    @Order(12)
    public void test12_getNonExistentPhysicianByIdAsAdmin() {
        logger.info("Executing test12_getNonExistentPhysicianByIdAsAdmin");

        int physicianId = 9999; // Assume this ID does not exist

        Response response = webTarget
                .path(PHYSICIAN_RESOURCE_NAME + "/" + physicianId)
                .register(adminAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Assert Status Code
        assertThat(response.getStatus(), is(404));
    }

    /**
     * Test Case ID: Physician_TC_13
     * 
     * Test Description: Verify that a regular user receives 403 Forbidden when accessing unauthorized physician details.
     * 
     * Preconditions:
     * - User with username 'cst8277' and password '8277' exists.
     * - A physician with a different ID exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 403 Forbidden.
     */
    @Test
    @Order(13)
    public void test13_getUnauthorizedPhysicianByIdAsUser() {
        logger.info("Executing test13_getUnauthorizedPhysicianByIdAsUser");

        int physicianId = 3; // Adjust to an ID not associated with 'cst8277'

        Response response = webTarget
                .path(PHYSICIAN_RESOURCE_NAME + "/" + physicianId)
                .register(userAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Assert Status Code
        assertThat(response.getStatus(), is(403));
    }

    /**
     * Test Case ID: Physician_TC_17
     * 
     * Test Description: Verify that updating a non-existent physician returns 404 Not Found.
     * 
     * Preconditions:
     * - Admin user with username 'admin' and password 'admin' exists.
     * - No physician exists with the specified ID.
     * 
     * Expected Result:
     * - HTTP Status 404 Not Found.
     */
    @Test
    @Order(17)
    public void test17_updateNonExistentPhysicianAsAdmin() {
        logger.info("Executing test17_updateNonExistentPhysicianAsAdmin");

        int physicianId = 7777; // Assume this ID does not exist
        Physician updatedPhysician = new Physician();
        updatedPhysician.setFirstName("NonExistent");
        updatedPhysician.setLastName("Physician");

        Response response = webTarget
                .path(PHYSICIAN_RESOURCE_NAME + "/" + physicianId)
                .register(adminAuth)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(updatedPhysician, MediaType.APPLICATION_JSON));

        // Assert Status Code
        assertThat(response.getStatus(), is(404));
    }

    /**
     * Test Case ID: Physician_TC_18
     * 
     * Test Description: Verify that accessing the PhysicianResource without authentication returns 401 Unauthorized.
     * 
     * Preconditions:
     * - No authentication credentials are provided.
     * 
     * Expected Result:
     * - HTTP Status 401 Unauthorized.
     */
    @Test
    @Order(18)
    public void test18_accessPhysicianResourceWithoutAuth() {
        logger.info("Executing test18_accessPhysicianResourceWithoutAuth");

        Response response = webTarget
                .path(PHYSICIAN_RESOURCE_NAME)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Assert Status Code
        assertThat(response.getStatus(), is(401));
    }

    /**
     * Test Case ID: Physician_TC_19
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
    public void test19_accessPhysicianResourceWithInvalidAuth() {
        logger.info("Executing test19_accessPhysicianResourceWithInvalidAuth");

        HttpAuthenticationFeature invalidAuth = HttpAuthenticationFeature.basic("invalidUser", "invalidPass");

        Response response = webTarget
                .path(PHYSICIAN_RESOURCE_NAME)
                .register(invalidAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Assert Status Code
        assertThat(response.getStatus(), is(401));
    }

    /**
     * Test Case ID: Physician_TC_20
     * 
     * Test Description: Verify that the PhysicianResource returns JSON content type.
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
    public void test20_physicianResourceContentType() {
        logger.info("Executing test20_physicianResourceContentType");

        Response response = webTarget
                .path(PHYSICIAN_RESOURCE_NAME)
                .register(adminAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Assert Status Code
        assertThat(response.getStatus(), is(200));

        // Assert Content-Type
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString("Content-Type"));
    }

    // Continue adding tests following the same pattern for the remaining test cases...

    // Example Placeholder for Additional Tests:
    // @Test
    // @Order(21)
    // public void test21_someOtherTest() {
    //     // Implement test
    // }

}
