package dev.morling.demos.jfrunit.user;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class UserResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/users/2")
          .then()
             .statusCode(200)
             .body(containsString("Alice"));
    }

}