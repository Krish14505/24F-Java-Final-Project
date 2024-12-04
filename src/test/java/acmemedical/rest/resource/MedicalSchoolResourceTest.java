///******************************************************************************************************** 
// * File:  MedicalSchoolResourceTest.java
// * Course Materials CST 8277
// *
// * Author: Harmeet Matharoo
// * Date: December 03, 2024
// ********************************************************************************************************/
//package acmemedical.rest.resource;
//
//import static acmemedical.utility.MyConstants.MEDICAL_SCHOOL_RESOURCE_NAME;
//import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import java.lang.invoke.MethodHandles;
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.List;
//
//import jakarta.ws.rs.client.Client;
//import jakarta.ws.rs.client.ClientBuilder;
//import jakarta.ws.rs.client.Entity;
//import jakarta.ws.rs.client.WebTarget;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//import jakarta.ws.rs.core.UriBuilder;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
//import org.glassfish.jersey.logging.LoggingFeature;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.junit.jupiter.api.TestInstance.Lifecycle;
//import org.junit.jupiter.api.TestMethodOrder;
//import org.junit.jupiter.api.MethodOrderer;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import acmemedical.MyObjectMapperProvider;
//import acmemedical.entity.MedicalSchool;
//
///**
// * Integration Tests for MedicalSchoolResource REST API.
// * 
// * <p>
// * This test suite covers CRUD operations for the MedicalSchool entity, including authentication and authorization scenarios.
// * </p>
// * 
// * <p>
// * Author: Harmeet Matharoo
// * Date: December 03, 2024
// * </p>
// */
//@TestInstance(Lifecycle.PER_CLASS)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class MedicalSchoolResourceTest {
//    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
//    private static final Logger logger = LogManager.getLogger(_thisClaz);
//
//    static final String HTTP_SCHEMA = "http";
//    static final String HOST = "localhost";
//    static final int PORT = 8080;
//
//    // Test fixture(s)
//    static URI uri;
//    static HttpAuthenticationFeature adminAuth;
//    static HttpAuthenticationFeature userAuth;
//
//    // List to track created MedicalSchool IDs for cleanup
//    private List<Integer> createdMedicalSchoolIds = new ArrayList<>();
//
//    @BeforeAll
//    public void oneTimeSetUp() throws Exception {
//        logger.debug("oneTimeSetUp");
//        uri = UriBuilder
//            .fromUri("http://localhost:8080/rest-acmemedical/api/v1")
//            .scheme(HTTP_SCHEMA)
//            .host(HOST)
//            .port(PORT)
//            .build();
//        adminAuth = HttpAuthenticationFeature.basic("admin", "admin");
//        userAuth = HttpAuthenticationFeature.basic("cst8277", "8277");
//    }
//
//    protected WebTarget webTarget;
//    protected ObjectMapper objectMapper;
//
//    @BeforeEach
//    public void setUp() {
//        Client client = ClientBuilder.newClient()
//                .register(MyObjectMapperProvider.class)
//                .register(new LoggingFeature());
//        webTarget = client.target(uri);
//        objectMapper = new ObjectMapper();
//    }
//
//    /**
//     * Helper method to create a MedicalSchool.
//     *
//     * @param name The name of the MedicalSchool.
//     * @return The created MedicalSchool object with an assigned ID.
//     */
//    private MedicalSchool createMedicalSchool(String name, boolean isPublic) {
//        MedicalSchool newMedicalSchool = new MedicalSchool() {
//            @Override
//            public boolean isPublic() {
//                return isPublic;
//            }
//        };
//        newMedicalSchool.setName(name);
//
//        Response response = webTarget
//                .path(MEDICAL_SCHOOL_RESOURCE_NAME)
//                .register(adminAuth)
//                .request(MediaType.APPLICATION_JSON)
//                .post(Entity.entity(newMedicalSchool, MediaType.APPLICATION_JSON));
//
//        // Assert POST response status
//        assertThat("Failed to create MedicalSchool", response.getStatus(), is(200));
//
//        // Deserialize response
//        MedicalSchool createdMedicalSchool = response.readEntity(MedicalSchool.class);
//
//        // Assert Response Body
//        assertNotNull(createdMedicalSchool.getId(), "MedicalSchool ID should not be null");
//        assertEquals(name, createdMedicalSchool.getName(), "MedicalSchool name should match");
//
//        // Track the created MedicalSchool ID for cleanup
//        createdMedicalSchoolIds.add(createdMedicalSchool.getId());
//
//        logger.debug("Created MedicalSchool with ID: {} and Name: {}", createdMedicalSchool.getId(), createdMedicalSchool.getName());
//
//        return createdMedicalSchool;
//    }
//
//    /**
//     * Helper method to delete a MedicalSchool by ID.
//     *
//     * @param medicalSchoolId The ID of the MedicalSchool to delete.
//     */
//    private void deleteMedicalSchool(int medicalSchoolId) {
//        Response response = webTarget
//                .path(MEDICAL_SCHOOL_RESOURCE_NAME + "/" + medicalSchoolId)
//                .register(adminAuth)
//                .request()
//                .delete();
//
//        if (response.getStatus() == 204 || response.getStatus() == 200) {
//            logger.debug("Successfully deleted MedicalSchool with ID: {}", medicalSchoolId);
//        } else {
//            logger.warn("Failed to delete MedicalSchool with ID: {}. Status: {}", medicalSchoolId, response.getStatus());
//        }
//    }
//
//    /**
//     * Test Case ID: MedicalSchool_TC_01
//     * 
//     * Test Description: Verify that an admin can retrieve all medical schools.
//     * 
//     * Preconditions:
//     * - Admin user with username 'admin' and password 'admin' exists.
//     * - At least one MedicalSchool exists in the database.
//     * 
//     * Expected Result:
//     * - HTTP Status 200 OK.
//     * - Response body contains a list of medical schools.
//     */
//    @Test
//    @Order(1)
//    public void test01_getAllMedicalSchoolsAsAdmin() {
//        logger.info("Executing test01_getAllMedicalSchoolsAsAdmin");
//
//        // Ensure at least one MedicalSchool exists
//        if (createdMedicalSchoolIds.isEmpty()) {
//            createMedicalSchool("Existing Medical School", true);
//        }
//
//        Response response = webTarget
//                .path(MEDICAL_SCHOOL_RESOURCE_NAME)
//                .register(adminAuth)
//                .request(MediaType.APPLICATION_JSON)
//                .get();
//
//        // Assert Status Code
//        assertThat(response.getStatus(), is(200));
//
//        // Deserialize response
//        MedicalSchool[] medicalSchools = response.readEntity(MedicalSchool[].class);
//
//        // Assert Response Body
//        assertNotNull(medicalSchools, "MedicalSchools list should not be null");
//        assertThat(medicalSchools.length > 0, is(true));
//
//        logger.debug("Retrieved {} MedicalSchools", medicalSchools.length);
//    }
//
//    /**
//     * Test Case ID: MedicalSchool_TC_02
//     * 
//     * Test Description: Verify that a regular user cannot retrieve all medical schools.
//     * 
//     * Preconditions:
//     * - User with username 'cst8277' and password '8277' exists.
//     * - At least one MedicalSchool exists in the database.
//     * 
//     * Expected Result:
//     * - HTTP Status 403 Forbidden.
//     */
//    @Test
//    @Order(2)
//    public void test02_getAllMedicalSchoolsAsUserForbidden() {
//        logger.info("Executing test02_getAllMedicalSchoolsAsUserForbidden");
//
//        // Ensure at least one MedicalSchool exists
//        if (createdMedicalSchoolIds.isEmpty()) {
//            createMedicalSchool("Existing Medical School", true);
//        }
//
//        Response response = webTarget
//                .path(MEDICAL_SCHOOL_RESOURCE_NAME)
//                .register(userAuth)
//                .request(MediaType.APPLICATION_JSON)
//                .get();
//
//        // Assert Status Code
//        assertThat(response.getStatus(), is(403));
//
//        logger.debug("User access to retrieve all MedicalSchools correctly forbidden.");
//    }
//
//    /**
//     * Test Case ID: MedicalSchool_TC_03
//     * 
//     * Test Description: Verify that an admin can retrieve a medical school by ID.
//     * 
//     * Preconditions:
//     * - Admin user with username 'admin' and password 'admin' exists.
//     * - A MedicalSchool with a valid ID exists in the database.
//     * 
//     * Expected Result:
//     * - HTTP Status 200 OK.
//     * - Response body contains the medical school details.
//     */
//    @Test
//    @Order(3)
//    public void test03_getMedicalSchoolByIdAsAdmin() {
//        logger.info("Executing test03_getMedicalSchoolByIdAsAdmin");
//
//        // Create a MedicalSchool to retrieve
//        MedicalSchool createdMedicalSchool = createMedicalSchool("Retrieve Medical School", false);
//        int medicalSchoolId = createdMedicalSchool.getId();
//
//        Response response = webTarget
//                .path(MEDICAL_SCHOOL_RESOURCE_NAME + "/" + medicalSchoolId)
//                .register(adminAuth)
//                .request(MediaType.APPLICATION_JSON)
//                .get();
//
//        // Assert Status Code
//        assertThat(response.getStatus(), is(200));
//
//        // Deserialize response
//        MedicalSchool medicalSchool = response.readEntity(MedicalSchool.class);
//
//        // Assert Response Body
//        assertNotNull(medicalSchool, "MedicalSchool should not be null");
//        assertEquals(medicalSchoolId, medicalSchool.getId(), "MedicalSchool ID should match");
//        assertEquals("Retrieve Medical School", medicalSchool.getName(), "MedicalSchool name should match");
//
//        logger.debug("Retrieved MedicalSchool: ID={}, Name={}", medicalSchool.getId(), medicalSchool.getName());
//    }
//
//    /**
//     * Test Case ID: MedicalSchool_TC_04
//     * 
//     * Test Description: Verify that a regular user can retrieve a medical school by ID.
//     * 
//     * Preconditions:
//     * - User with username 'cst8277' and password '8277' exists.
//     * - A MedicalSchool with a valid ID exists in the database.
//     * 
//     * Expected Result:
//     * - HTTP Status 200 OK.
//     * - Response body contains the medical school details.
//     */
//    @Test
//    @Order(4)
//    public void test04_getMedicalSchoolByIdAsUser() {
//        logger.info("Executing test04_getMedicalSchoolByIdAsUser");
//
//        // Create a MedicalSchool to retrieve
//        MedicalSchool createdMedicalSchool = createMedicalSchool("User Retrieve Medical School", true);
//        int medicalSchoolId = createdMedicalSchool.getId();
//
//        Response response = webTarget
//                .path(MEDICAL_SCHOOL_RESOURCE_NAME + "/" + medicalSchoolId)
//                .register(userAuth)
//                .request(MediaType.APPLICATION_JSON)
//                .get();
//
//        // Assert Status Code
//        assertThat(response.getStatus(), is(200));
//
//        // Deserialize response
//        MedicalSchool medicalSchool = response.readEntity(MedicalSchool.class);
//
//        // Assert Response Body
//        assertNotNull(medicalSchool, "MedicalSchool should not be null");
//        assertEquals(medicalSchoolId, medicalSchool.getId(), "MedicalSchool ID should match");
//        assertEquals("User Retrieve Medical School", medicalSchool.getName(), "MedicalSchool name should match");
//
//        logger.debug("User retrieved MedicalSchool: ID={}, Name={}", medicalSchool.getId(), medicalSchool.getName());
//    }
//
//    /**
//     * Test Case ID: MedicalSchool_TC_05
//     * 
//     * Test Description: Verify that an admin can add a new medical school.
//     * 
//     * Preconditions:
//     * - Admin user with username 'admin' and password 'admin' exists.
//     * - The MedicalSchool "Stanford Medical School" does not already exist in the database.
//     * 
//     * Expected Result:
//     * - HTTP Status 200 OK.
//     * - Response body contains the created medical school with an assigned ID.
//     */
//    @Test
//    @Order(5)
//    public void test05_addNewMedicalSchoolAsAdmin() {
//        logger.info("Executing test05_addNewMedicalSchoolAsAdmin");
//
//        String newSchoolName = "Stanford Medical School";
//
//        MedicalSchool newMedicalSchool = new MedicalSchool() {
//            @Override
//            public boolean isPublic() {
//                return true; // Assuming PublicSchool
//            }
//        };
//        newMedicalSchool.setName(newSchoolName);
//
//        logger.debug("Sending new MedicalSchool creation request: {}", newMedicalSchool);
//
//        Response response = webTarget
//                .path(MEDICAL_SCHOOL_RESOURCE_NAME)
//                .register(adminAuth)
//                .request(MediaType.APPLICATION_JSON)
//                .post(Entity.entity(newMedicalSchool, MediaType.APPLICATION_JSON));
//
//        // Assert Status Code
//        assertThat("Failed to create MedicalSchool", response.getStatus(), is(200));
//
//        // Deserialize response
//        MedicalSchool createdMedicalSchool = response.readEntity(MedicalSchool.class);
//
//        // Assert Response Body
//        assertNotNull(createdMedicalSchool.getId(), "MedicalSchool ID should not be null");
//        assertEquals(newSchoolName, createdMedicalSchool.getName(), "MedicalSchool name should match");
//
//        // Track the created MedicalSchool ID for cleanup
//        createdMedicalSchoolIds.add(createdMedicalSchool.getId());
//
//        logger.debug("Created MedicalSchool with ID: {} and Name: {}", createdMedicalSchool.getId(), createdMedicalSchool.getName());
//    }
//
//    /**
//     * Test Case ID: MedicalSchool_TC_06
//     * 
//     * Test Description: Verify that a regular user cannot add a new medical school.
//     * 
//     * Preconditions:
//     * - User with username 'cst8277' and password '8277' exists.
//     * 
//     * Expected Result:
//     * - HTTP Status 403 Forbidden.
//     */
//    @Test
//    @Order(6)
//    public void test06_addNewMedicalSchoolAsUserForbidden() {
//        logger.info("Executing test06_addNewMedicalSchoolAsUserForbidden");
//
//        MedicalSchool newMedicalSchool = new MedicalSchool() {
//            @Override
//            public boolean isPublic() {
//                return false; // Assuming PrivateSchool
//            }
//        };
//        newMedicalSchool.setName("MIT Medical School");
//
//        logger.debug("User attempting to create MedicalSchool: {}", newMedicalSchool);
//
//        Response response = webTarget
//                .path(MEDICAL_SCHOOL_RESOURCE_NAME)
//                .register(userAuth)
//                .request(MediaType.APPLICATION_JSON)
//                .post(Entity.entity(newMedicalSchool, MediaType.APPLICATION_JSON));
//
//        // Assert Status Code
//        assertThat(response.getStatus(), is(403));
//
//        logger.debug("User correctly forbidden from creating MedicalSchool.");
//    }
//
//    /**
//     * Test Case ID: MedicalSchool_TC_07
//     * 
//     * Test Description: Verify that an admin can update an existing medical school by ID.
//     * 
//     * Preconditions:
//     * - Admin user with username 'admin' and password 'admin' exists.
//     * - A MedicalSchool with a valid ID exists in the database.
//     * 
//     * Expected Result:
//     * - HTTP Status 200 OK.
//     * - Response body contains the updated medical school details.
//     */
//    @Test
//    @Order(7)
//    public void test07_updateMedicalSchoolByIdAsAdmin() {
//        logger.info("Executing test07_updateMedicalSchoolByIdAsAdmin");
//
//        // Create a MedicalSchool to update
//        MedicalSchool createdMedicalSchool = createMedicalSchool("Update Medical School", true);
//        int medicalSchoolId = createdMedicalSchool.getId();
//
//        // Prepare updated MedicalSchool data
//        String updatedName = "Updated Medical School Name";
//        MedicalSchool updatedMedicalSchool = new MedicalSchool() {
//            @Override
//            public boolean isPublic() {
//                return createdMedicalSchool.isPublic(); // Preserve the original isPublic status
//            }
//        };
//        updatedMedicalSchool.setName(updatedName);
//
//        logger.debug("Sending updated MedicalSchool: {}", updatedMedicalSchool);
//
//        Response response = webTarget
//                .path(MEDICAL_SCHOOL_RESOURCE_NAME + "/" + medicalSchoolId)
//                .register(adminAuth)
//                .request(MediaType.APPLICATION_JSON)
//                .put(Entity.entity(updatedMedicalSchool, MediaType.APPLICATION_JSON));
//
//        // Assert Status Code
//        assertThat("Failed to update MedicalSchool", response.getStatus(), is(200));
//
//        // Deserialize response
//        MedicalSchool medicalSchool = response.readEntity(MedicalSchool.class);
//
//        // Assert Response Body
//        assertNotNull(medicalSchool, "MedicalSchool should not be null");
//        assertEquals(medicalSchoolId, medicalSchool.getId(), "MedicalSchool ID should match");
//        assertEquals(updatedName, medicalSchool.getName(), "MedicalSchool name should be updated");
//
//        logger.debug("Updated MedicalSchool: ID={}, Name={}", medicalSchool.getId(), medicalSchool.getName());
//    }
//
//    /**
//     * Test Case ID: MedicalSchool_TC_08
//     * 
//     * Test Description: Verify that a regular user cannot update an existing medical school by ID.
//     * 
//     * Preconditions:
//     * - User with username 'cst8277' and password '8277' exists.
//     * - A MedicalSchool with a valid ID exists in the database.
//     * 
//     * Expected Result:
//     * - HTTP Status 403 Forbidden.
//     */
//    @Test
//    @Order(8)
//    public void test08_updateMedicalSchoolByIdAsUserForbidden() {
//        logger.info("Executing test08_updateMedicalSchoolByIdAsUserForbidden");
//
//        // Create a MedicalSchool to attempt update
//        MedicalSchool createdMedicalSchool = createMedicalSchool("User Update Attempt School", false);
//        int medicalSchoolId = createdMedicalSchool.getId();
//
//        // Prepare updated MedicalSchool data
//        String updatedName = "User Attempted Update Name";
//        MedicalSchool updatedMedicalSchool = new MedicalSchool() {
//            @Override
//            public boolean isPublic() {
//                return createdMedicalSchool.isPublic(); // Preserve the original isPublic status
//            }
//        };
//        updatedMedicalSchool.setName(updatedName);
//
//        logger.debug("User attempting to update MedicalSchool: {}", updatedMedicalSchool);
//
//        Response response = webTarget
//                .path(MEDICAL_SCHOOL_RESOURCE_NAME + "/" + medicalSchoolId)
//                .register(userAuth)
//                .request(MediaType.APPLICATION_JSON)
//                .put(Entity.entity(updatedMedicalSchool, MediaType.APPLICATION_JSON));
//
//        // Assert Status Code
//        assertThat(response.getStatus(), is(403));
//
//        logger.debug("User correctly forbidden from updating MedicalSchool.");
//    }
//
//    /**
//     * Test Case ID: MedicalSchool_TC_09
//     * 
//     * Test Description: Verify that an admin can delete a medical school by ID.
//     * 
//     * Preconditions:
//     * - Admin user with username 'admin' and password 'admin' exists.
//     * - A MedicalSchool with a valid ID exists in the database.
//     * 
//     * Expected Result:
//     * - HTTP Status 204 No Content.
//     * - MedicalSchool is removed from the database.
//     */
//    @Test
//    @Order(9)
//    public void test09_deleteMedicalSchoolByIdAsAdmin() {
//        logger.info("Executing test09_deleteMedicalSchoolByIdAsAdmin");
//
//        // Create a MedicalSchool to delete
//        MedicalSchool createdMedicalSchool = createMedicalSchool("Delete Medical School", true);
//        int medicalSchoolId = createdMedicalSchool.getId();
//
//        Response response = webTarget
//                .path(MEDICAL_SCHOOL_RESOURCE_NAME + "/" + medicalSchoolId)
//                .register(adminAuth)
//                .request()
//                .delete();
//
//        // Assert Status Code
//        assertThat("Failed to delete MedicalSchool", response.getStatus(), is(204));
//
//        logger.debug("Deleted MedicalSchool with ID: {}", medicalSchoolId);
//
//        // Optionally, verify deletion by attempting to retrieve it
//        Response getResponse = webTarget
//                .path(MEDICAL_SCHOOL_RESOURCE_NAME + "/" + medicalSchoolId)
//                .register(adminAuth)
//                .request(MediaType.APPLICATION_JSON)
//                .get();
//
//        assertThat("Deleted MedicalSchool should not be found", getResponse.getStatus(), is(404));
//
//        logger.debug("Verified deletion by confirming MedicalSchool with ID {} is not found.", medicalSchoolId);
//    }
//
//    /**
//     * Test Case ID: MedicalSchool_TC_10
//     * 
//     * Test Description: Verify that a regular user cannot delete a medical school by ID.
//     * 
//     * Preconditions:
//     * - User with username 'cst8277' and password '8277' exists.
//     * - A MedicalSchool with a valid ID exists in the database.
//     * 
//     * Expected Result:
//     * - HTTP Status 403 Forbidden.
//     */
//    @Test
//    @Order(10)
//    public void test10_deleteMedicalSchoolByIdAsUserForbidden() {
//        logger.info("Executing test10_deleteMedicalSchoolByIdAsUserForbidden");
//
//        // Create a MedicalSchool to attempt deletion
//        MedicalSchool createdMedicalSchool = createMedicalSchool("User Delete Attempt School", false);
//        int medicalSchoolId = createdMedicalSchool.getId();
//
//        Response response = webTarget
//                .path(MEDICAL_SCHOOL_RESOURCE_NAME + "/" + medicalSchoolId)
//                .register(userAuth)
//                .request()
//                .delete();
//
//        // Assert Status Code
//        assertThat(response.getStatus(), is(403));
//
//        logger.debug("User correctly forbidden from deleting MedicalSchool.");
//    }
//
//    /**
//     * Cleanup method to remove any MedicalSchool entities created during tests.
//     */
//    @AfterAll
//    public void cleanup() {
//        logger.info("Executing cleanup after all tests");
//
//        for (Integer msId : createdMedicalSchoolIds) {
//            logger.debug("Deleting MedicalSchool with ID: {}", msId);
//            deleteMedicalSchool(msId);
//        }
//
//        logger.info("Cleanup completed.");
//    }
//}
