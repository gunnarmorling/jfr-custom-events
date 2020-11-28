package dev.morling.demos.quarkus;

import static dev.morling.demos.quarkus.testutil.MatchesJson.matchesJson;
import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import dev.morling.demos.quarkus.testutil.PostgresResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@QuarkusTestResource(PostgresResource.class)
public class TodoResourceTest {

    @Test
    public void testGETHelloEndpoint() {
        given()
            .when()
                .contentType(ContentType.JSON)
                .body("""
                      {
                        "title" : "Learn Quarkus",
                        "priority" : 1,
                      }
                      """)
            .then()
                .statusCode(201)
                .body(
                     matchesJson(
                        """
                        {
                          "id" : 1",
                          "title" : "Learn Quarkus",
                          "priority" : 1,
                          "completed" : false,
                        }
                        """
                ));
    }

}
