/********************************************************************************************************
 * File:  MedicineResourceTest.java
 * Course Materials CST 8277
 *
 * Author: Harmeet Matharoo
 * Date: December 03, 2024
 ********************************************************************************************************/
package acmemedical.rest.resource;

import static acmemedical.utility.MyConstants.MEDICINE_RESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.invoke.MethodHandles;
import java.net.URI;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import com.fasterxml.jackson.databind.ObjectMapper;

import acmemedical.MyObjectMapperProvider;
import acmemedical.entity.Medicine;

/**
 * Integration Tests for MedicineResource REST API.
 * 
 * <p>
 * This test suite covers CRUD operations for the Medicine entity, including authentication and authorization scenarios.
 * </p>
 * 
 * <p>
 * Author: Harmeet Matharoo
 * Date: December 03, 2024
 * </p>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MedicineResourceTest {
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
     * Test Case ID: Medicine_TC_01
     * 
     * Test Description: Verify that an admin can retrieve all medicines.
     * 
     * Preconditions:
     * - Admin user with username 'admin' and password 'admin' exists.
     * - At least one Medicine exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 200 OK.
     * - Response body contains a list of medicines.
     */
    @Test
    @Order(1)
    public void test01_getAllMedicinesAsAdmin() {
        logger.info("Executing test01_getAllMedicinesAsAdmin");

        Response response = webTarget
                .path(MEDICINE_RESOURCE_NAME)
                .register(adminAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Assert Status Code
        assertThat(response.getStatus(), is(200));

        // Deserialize response
        Medicine[] medicines = response.readEntity(Medicine[].class);

        // Assert Response Body
        assertNotNull(medicines, "Medicines list should not be null");
        assertThat(medicines.length > 0, is(true));
    }

    /**
     * Test Case ID: Medicine_TC_02
     * 
     * Test Description: Verify that a regular user can retrieve all medicines.
     * 
     * Preconditions:
     * - User with username 'cst8277' and password '8277' exists.
     * - At least one Medicine exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 200 OK.
     * - Response body contains a list of medicines.
     */
    @Test
    @Order(2)
    public void test02_getAllMedicinesAsUser() {
        logger.info("Executing test02_getAllMedicinesAsUser");

        Response response = webTarget
                .path(MEDICINE_RESOURCE_NAME)
                .register(userAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Assert Status Code
        assertThat(response.getStatus(), is(403));
    }

    /**
     * Test Case ID: Medicine_TC_03
     * 
     * Test Description: Verify that an admin can retrieve a medicine by ID.
     * 
     * Preconditions:
     * - Admin user with username 'admin' and password 'admin' exists.
     * - A Medicine with ID 1 exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 200 OK.
     * - Response body contains the medicine details.
     */
    @Test
    @Order(3)
    public void test03_getMedicineByIdAsAdmin() {
        logger.info("Executing test03_getMedicineByIdAsAdmin");

        int medicineId = 1; // Ensure this Medicine ID exists

        Response response = webTarget
                .path(MEDICINE_RESOURCE_NAME + "/" + medicineId)
                .register(adminAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Assert Status Code
        assertThat(response.getStatus(), is(200));

        // Deserialize response
        Medicine medicine = response.readEntity(Medicine.class);

        // Assert Response Body
        assertNotNull(medicine, "Medicine should not be null");
        assertEquals(medicineId, medicine.getId(), "Medicine ID should match");
        assertNotNull(medicine.getDrugName(), "Drug name should not be null");
    }

    /**
     * Test Case ID: Medicine_TC_04
     * 
     * Test Description: Verify that a regular user can retrieve a medicine by ID.
     * 
     * Preconditions:
     * - User with username 'cst8277' and password '8277' exists.
     * - A Medicine with ID 1 exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 200 OK.
     * - Response body contains the medicine details.
     */
    @Test
    @Order(4)
    public void test04_getMedicineByIdAsUser() {
        logger.info("Executing test04_getMedicineByIdAsUser");

        int medicineId = 1; // Ensure this Medicine ID exists

        Response response = webTarget
                .path(MEDICINE_RESOURCE_NAME + "/" + medicineId)
                .register(userAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        // Assert Status Code
        assertThat(response.getStatus(), is(200));

        // Deserialize response
        Medicine medicine = response.readEntity(Medicine.class);

        // Assert Response Body
        assertNotNull(medicine, "Medicine should not be null");
        assertEquals(medicineId, medicine.getId(), "Medicine ID should match");
        assertNotNull(medicine.getDrugName(), "Drug name should not be null");
    }

    /**
     * Test Case ID: Medicine_TC_05
     * 
     * Test Description: Verify that an admin can add a new medicine.
     * 
     * Preconditions:
     * - Admin user with username 'admin' and password 'admin' exists.
     * - The Medicine "Biogesic" does not already exist in the database.
     * 
     * Expected Result:
     * - HTTP Status 201 Created.
     * - Response body contains the created medicine with an assigned ID.
     */
    @Test
    @Order(5)
    public void test05_addNewMedicineAsAdmin() {
        logger.info("Executing test05_addNewMedicineAsAdmin");

        Medicine newMedicine = new Medicine();
        newMedicine.setDrugName("Biogesic");
        newMedicine.setManufacturerName("Unilab");
        newMedicine.setDosageInformation("Take 4 tablets per day with 6 hours interval");
        newMedicine.setGenericName("Paracetamol");
        newMedicine.setChemicalName(null); // Assuming chemicalName is optional

        Response response = webTarget
                .path(MEDICINE_RESOURCE_NAME)
                .register(adminAuth)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(newMedicine, MediaType.APPLICATION_JSON));

        // Assert Status Code
        assertThat(response.getStatus(), is(201));

        // Buffer the entity to allow multiple reads
        response.bufferEntity();

        // Deserialize response
        Medicine createdMedicine = response.readEntity(Medicine.class);

        // Assert Response Body
        assertNotNull(createdMedicine.getId(), "Medicine ID should not be null");
        assertEquals("Biogesic", createdMedicine.getDrugName(), "Drug name should match");
        assertEquals("Unilab", createdMedicine.getManufacturerName(), "Manufacturer name should match");
        assertEquals("Take 4 tablets per day with 6 hours interval", createdMedicine.getDosageInformation(), "Dosage information should match");
        assertEquals("Paracetamol", createdMedicine.getGenericName(), "Generic name should match");
    }

    /**
     * Test Case ID: Medicine_TC_06
     * 
     * Test Description: Verify that a regular user cannot add a new medicine.
     * 
     * Preconditions:
     * - User with username 'cst8277' and password '8277' exists.
     * 
     * Expected Result:
     * - HTTP Status 403 Forbidden.
     */
    @Test
    @Order(6)
    public void test06_addNewMedicineAsUserForbidden() {
        logger.info("Executing test06_addNewMedicineAsUserForbidden");

        Medicine newMedicine = new Medicine();
        newMedicine.setDrugName("Biogesic");
        newMedicine.setManufacturerName("Unilab");
        newMedicine.setDosageInformation("Take 4 tablets per day with 6 hours interval");
        newMedicine.setGenericName("Paracetamol");
        newMedicine.setChemicalName(null); // Assuming chemicalName is optional

        Response response = webTarget
                .path(MEDICINE_RESOURCE_NAME)
                .register(userAuth)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(newMedicine, MediaType.APPLICATION_JSON));

        // Assert Status Code
        assertThat(response.getStatus(), is(403));
    }

    /**
     * Test Case ID: Medicine_TC_07
     * 
     * Test Description: Verify that an admin can update an existing medicine by ID.
     * 
     * Preconditions:
     * - Admin user with username 'admin' and password 'admin' exists.
     * - A Medicine with ID 1 exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 200 OK.
     * - Response body contains the updated medicine details.
     */
    @Test
    @Order(7)
    public void test07_updateMedicineByIdAsAdmin() {
        logger.info("Executing test07_updateMedicineByIdAsAdmin");

        int medicineId = 1; // Ensure this Medicine ID exists

        Medicine updatedMedicine = new Medicine();
        updatedMedicine.setDrugName("New Drug");
        updatedMedicine.setManufacturerName("Unilab");
        updatedMedicine.setDosageInformation("Take 4 tablets per day with 6 hours interval");
        updatedMedicine.setGenericName("Paracetamol");
        updatedMedicine.setChemicalName(null); // Assuming chemicalName is optional

        Response response = webTarget
                .path(MEDICINE_RESOURCE_NAME + "/" + medicineId)
                .register(adminAuth)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(updatedMedicine, MediaType.APPLICATION_JSON));

        // Assert Status Code
        assertThat(response.getStatus(), is(200));

        // Deserialize response
        Medicine medicine = response.readEntity(Medicine.class);

        // Assert Response Body
        assertNotNull(medicine, "Medicine should not be null");
        assertEquals(medicineId, medicine.getId(), "Medicine ID should match");
        assertEquals("New Drug", medicine.getDrugName(), "Drug name should be updated");
    }

    /**
     * Test Case ID: Medicine_TC_08
     * 
     * Test Description: Verify that a regular user cannot update a medicine by ID.
     * 
     * Preconditions:
     * - User with username 'cst8277' and password '8277' exists.
     * - A Medicine with ID 1 exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 403 Forbidden.
     */
    @Test
    @Order(8)
    public void test08_updateMedicineByIdAsUserForbidden() {
        logger.info("Executing test08_updateMedicineByIdAsUserForbidden");

        int medicineId = 1; // Ensure this Medicine ID exists

        Medicine updatedMedicine = new Medicine();
        updatedMedicine.setDrugName("Unauthorized Update");
        updatedMedicine.setManufacturerName("Unilab");
        updatedMedicine.setDosageInformation("Take 4 tablets per day with 6 hours interval");
        updatedMedicine.setGenericName("Paracetamol");
        updatedMedicine.setChemicalName(null); // Assuming chemicalName is optional

        Response response = webTarget
                .path(MEDICINE_RESOURCE_NAME + "/" + medicineId)
                .register(userAuth)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(updatedMedicine, MediaType.APPLICATION_JSON));

        // Assert Status Code
        assertThat(response.getStatus(), is(403));
    }

    /**
     * Test Case ID: Medicine_TC_09
     * 
     * Test Description: Verify that an admin can delete a medicine by ID.
     * 
     * Preconditions:
     * - Admin user with username 'admin' and password 'admin' exists.
     * - A Medicine with ID 3 exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 204 No Content.
     * - Medicine is removed from the database.
     */
    @Test
    @Order(9)
    public void test09_deleteMedicineByIdAsAdmin() {
        logger.info("Executing test09_deleteMedicineByIdAsAdmin");

        int medicineId = 3; // Ensure this Medicine ID exists

        Response response = webTarget
                .path(MEDICINE_RESOURCE_NAME + "/" + medicineId)
                .register(adminAuth)
                .request()
                .delete();

        // Assert Status Code
        assertThat(response.getStatus(), is(204));

        // Verify Deletion by attempting to retrieve the deleted Medicine
        Response getResponse = webTarget
                .path(MEDICINE_RESOURCE_NAME + "/" + medicineId)
                .register(adminAuth)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertThat(getResponse.getStatus(), is(404));
    }

    /**
     * Test Case ID: Medicine_TC_10
     * 
     * Test Description: Verify that a regular user cannot delete a medicine by ID.
     * 
     * Preconditions:
     * - User with username 'cst8277' and password '8277' exists.
     * - A Medicine with ID 3 exists in the database.
     * 
     * Expected Result:
     * - HTTP Status 403 Forbidden.
     */
    @Test
    @Order(10)
    public void test10_deleteMedicineByIdAsUserForbidden() {
        logger.info("Executing test10_deleteMedicineByIdAsUserForbidden");

        int medicineId = 3; // Ensure this Medicine ID exists

        Response response = webTarget
                .path(MEDICINE_RESOURCE_NAME + "/" + medicineId)
                .register(userAuth)
                .request()
                .delete();

        // Assert Status Code
        assertThat(response.getStatus(), is(403));
    }
}
