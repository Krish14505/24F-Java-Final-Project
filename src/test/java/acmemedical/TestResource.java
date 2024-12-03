package acmemedical;


import io.restassured.http.Header;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.util.Base64;

/**
 * This is the test case file for the Api request via postman
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestResource {
    private static final String BASE_URL = "http://localhost:8080/rest-acmemedical/api/v1";
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String USER = "cst8277";
    private static final String USER_PASSWORD = "8277";

    /**
     * Creating the basic user Authentication headers
     */
    private static final String ADMIN_AUTHENTICATION = Base64.getEncoder().encodeToString((ADMIN_USER + ":" + ADMIN_PASSWORD).getBytes());
    private static final String USER_AUTHENTICATION = Base64.getEncoder().encodeToString((USER + ":" + USER_PASSWORD).getBytes());

    /**
     * Creating the instance of the header
     */

    private static final Header ADMIN_AUTHENTICATION_HEADER = new Header("Authorisation","basic"+ ADMIN_AUTHENTICATION);
    private static final Header USER_AUTHENTICATION_HEADER = new Header("Authorization","basic " + USER_AUTHENTICATION);


    /**
     * providing the set before running the test case
     */
    @BeforeAll
    public void setup(){
        baseURI = BASE_URL;
    }

    /**
     *  The following tests are for the physician class
     */
    public void testGetAllPhysicians(){
        given().aut
    }
}
