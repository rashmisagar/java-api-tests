package utils;

import java.util.Random;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.equalTo;

public class Utilities {

    public String generateRandomProductName() {
        Random random = new Random();
        int randomNum = random.nextInt(1000);  // Generate a random number between 0 and 999
        return "Laptop-" + randomNum;
    }

    public void assertResponse(Response response, int expectedStatusCode, boolean expectedSuccess, String expectedMessage) {
        response.then()
                .statusCode(expectedStatusCode)
                .body("success", equalTo(expectedSuccess))
                .body("message", equalTo(expectedMessage));
    }

}
