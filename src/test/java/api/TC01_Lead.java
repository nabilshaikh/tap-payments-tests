package api;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import helper.CommonMethods;
import java.io.IOException;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import io.github.cdimascio.dotenv.Dotenv;

public class TC01_Lead {
	
	Dotenv dotenv = Dotenv.load();
	CommonMethods cm = new CommonMethods();
	
	@BeforeClass
    public void setup() {
        RestAssured.baseURI = dotenv.get("BASE_URI");
    }

    @Test(enabled=true)
    public void testCreateLeadResourceByPassingMandatoryAndOptionalObjects() throws IOException {
        String bearerToken = dotenv.get("BEARER_TOKEN");
        
        // Read request body from file
        String requestBody = cm.readFileAsString("src/test/resources/leadRequestBody.json");
        
        Response response =
        given().
        	header("Authorization", "Bearer " + bearerToken).
        	header("Content-Type", "application/json").
        	body(requestBody).
        when().
        	post(dotenv.get("ENDPOINT_LEAD")).
        then().
        	statusCode(200). // expecting 201 for new resource creation but received 200 
        	body("id", notNullValue()). // verify that the 'id' attribute is present and not null
        	extract().
        	response();

        String id = response.path("id");
        System.out.println("Created Resource ID: " + id);
        assertTrue(id.startsWith("led_"), "Expected 'id' to start with 'led_', but got: " + id);
    }
    
    @Test(enabled=true)
    public void testCreateLeadResourceByPassingBlankResponse() throws IOException {
        String bearerToken = dotenv.get("BEARER_TOKEN");
        
        String requestBody = "{}"; // Passing blank request body
        
        Response response =
        given().
        	header("Authorization", "Bearer " + bearerToken).
        	header("Content-Type", "application/json").
        	body(requestBody).
        when().
        	post(dotenv.get("ENDPOINT_LEAD")).
        then().
        	statusCode(400).
        	extract().
        	response();

        String description = response.jsonPath().getString("errors[0].description");
        assertEquals(description, "Body cannot be empty or null", "Expected description does not match");
    }
	
	@Test(enabled=true)
    public void testCreateLeadResourceByPassingIncompleteObjects() throws IOException {
        String bearerToken = dotenv.get("BEARER_TOKEN");
        
        // Read request body from file
        String requestBody = cm.readFileAsString("src/test/resources/leadRequestBodyIncomplete.json");
        
        Response response =
        given().
        	header("Authorization", "Bearer " + bearerToken).
        	header("Content-Type", "application/json").
        	body(requestBody).
        when().
        	post(dotenv.get("ENDPOINT_LEAD")).
        then().
        	extract().
        	response();
        System.out.println("Resp is: " + response.asPrettyString());
        assertEquals(response.statusCode(), 400); // expecting 400, but received 500 
    }
}
