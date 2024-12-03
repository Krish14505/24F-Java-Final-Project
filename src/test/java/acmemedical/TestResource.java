package acmemedical;

import static io.restassured.RestAssured.*;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.*;

import java.util.Base64;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import io.restassured.http.ContentType;
import io.restassured.http.Header;

@TestInstance(Lifecycle.PER_CLASS)
public class TestResource {

    private static final String BASE_URL = "http://localhost:8080/rest-acmemedical/api/v1";
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String USER = "cst8277";
    private static final String USER_PASSWORD = "8277";

    // Create Basic Auth headers
    private static final String ADMIN_AUTH = Base64.getEncoder()
            .encodeToString((ADMIN_USER + ":" + ADMIN_PASSWORD).getBytes());
    private static final String USER_AUTH = Base64.getEncoder()
            .encodeToString((USER + ":" + USER_PASSWORD).getBytes());

    private static final Header ADMIN_AUTH_HEADER = new Header("Authorization", "Basic " + ADMIN_AUTH);
    private static final Header USER_AUTH_HEADER = new Header("Authorization", "Basic " + USER_AUTH);

    @BeforeAll
    public void setup() {
        baseURI = BASE_URL;
    }

    /*
     * The following tests are for the physicianResource requests.
     */

    /**
     * This method is for testing that GET request for the physician is working as expected!
     */
    @Test
    public void testGetAllPhysicians(){
        given().auth().basic(ADMIN_USER, ADMIN_PASSWORD)
                .header(ADMIN_AUTH_HEADER)
                .when().get("/physicians")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()",greaterThan(0));
    }

    /**
     * This test method is used for the GET request by retrieving the specific physician.
     */
    @Test
    public void testGetPhysicianById(){
        given().auth().basic(ADMIN_USER, ADMIN_PASSWORD)
                .header(ADMIN_AUTH_HEADER)
                .when().get("/physicians/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()",equalTo(1));
    }

    /**
     * This test method is used to create the physician using POST request to the resource.
     */
    @Test
    public void testCreatePhysician(){
        String requestForCreatingPhysician = """
                    {
                        "firstName": "Krish",
                        "lastName": "Chaudhary"
                    }
                """;

        given().auth().basic(ADMIN_USER, ADMIN_PASSWORD)
                .header(ADMIN_AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(requestForCreatingPhysician)
                .when().post("/physician")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("firstName",equalTo("Krish"))
                .body("lastName",equalTo("Chaudhary"));

    }

    /**
     * The following is used to test when the current security user is authorised or not.
     */
    @Test
    public void testUserAccessToSpecificPhysician(){
            given()
                    .auth().basic(ADMIN_USER, ADMIN_PASSWORD)
                    .header(ADMIN_AUTH_HEADER)
                    .when().get("/physicians/1")
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
    }

    /*
     * The following test methods are for the patient class
     */

    /**
     * The method used to retrieve all the patients
     */
    @Test
    public void testGetAllPatients(){
        given().auth().basic(ADMIN_USER, ADMIN_PASSWORD).header(ADMIN_AUTH_HEADER)
                .when().get("/patient")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()",greaterThan(0));
    }

    /**
     * The method used to test retrieving the specific Patient
     */
    @Test
    public void testGetPatientById(){
        given().auth().basic(ADMIN_USER, ADMIN_PASSWORD).header(ADMIN_AUTH_HEADER)
                .when().get("/patient/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(1));
    }

    /**
     * The mentioned method is used to test to create a patient record !
     */
    @Test
    public void testCreatePatient(){
        String requestToAddNewPatient = """
                    {
                        "firstName" : "Steve",
                        "lastName" : "Smith",
                        "year" : 1980,
                        "address" : "354 elgin st.",
                        "height" : 187,
                        "weight" : 78,
                        "smoker" : 0
                    }
                """;

        given().auth().basic(ADMIN_USER, ADMIN_PASSWORD).header(ADMIN_AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(requestToAddNewPatient)
                .when().post("/patient")
                .then()
                .statusCode(200)
                .body("firstName",equalTo("Steve"))
                .body("lastName",equalTo("Smith"));
    }

    /*
     *The following test methods are for the Medicine.
     */

    /**
     * creating the test for retrieving all the medicine classes
     */

    @Test
    public void testGetAllMedicine(){
        given()
            .header(ADMIN_AUTH_HEADER)
                .when().get("/medicine")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()",greaterThan(0));

    }

    @Test
    public void testGetMedicineById(){
        given()
                .header(ADMIN_AUTH_HEADER)
                .when().get("/medicine/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(1));
    }

    @Test
    public void testCreateMedicine(){
        String requestToCreateNewMedicine = """
                 {
                     "drugName" : "Biogesic",
                     "manufacturerName": "Unilab",
                     "dosageInformation": "Take 4 tablets per day with 6 hours interval",
                     "chemicalName": null,
                     "genericName": "Paracetamol"   
                 }
                """;

        given()
                .header(ADMIN_AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(requestToCreateNewMedicine)
                .when().post("/medicine")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("drugName",equalTo("Biogesic"));
    }

    /*
     * Test cases for the MedicalSchool class
     */

    @Test
    public void testGetAllMedicalSchools(){
        given()
                .header(ADMIN_AUTH_HEADER)
                .when().get("/medicalschool")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()",greaterThan(0));
    }

    @Test
    public void testGetMedicalSchoolById(){
        given()
            .header(ADMIN_AUTH_HEADER)
                .when().get("/medicalschool/2")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(2));
    }

    @Test
    public void testCreateMedicalSchool(){
        String requestToCreateNewMedicalSchool = """
                {
                    "isPublic" : false,
                    "name": "US Medical School"
                }
                """;

        given()
                .header(ADMIN_AUTH_HEADER)
                .contentType(ContentType.JSON)
                .body(requestToCreateNewMedicalSchool)
                .when().post("/medicalschool")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }
}
