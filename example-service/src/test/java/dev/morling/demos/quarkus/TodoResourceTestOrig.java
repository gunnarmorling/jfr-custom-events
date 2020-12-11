package dev.morling.demos.quarkus;

import static dev.morling.demos.quarkus.testutil.MatchesJson.matchesJson;
import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import dev.morling.demos.quarkus.testutil.PostgresResource;
import dev.morling.jfrunit.EnableEvent;
import dev.morling.jfrunit.JfrEvents;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@QuarkusTestResource(PostgresResource.class)
public class TodoResourceTestOrig {

    public JfrEvents jfrEvents = new JfrEvents();

    @Test
    @Order(1)
    public void createTodo() {
        given()
        .when()
            .body("""
                  {
                    "title" : "Learn Quarkus",
                    "priority" : 1
                  }
                  """)
            .contentType(ContentType.JSON)
            .post("/todo")
        .then()
            .statusCode(201)
            .body(
                 matchesJson(
                    """
                    {
                      "id" : 1,
                      "completed" : false,
                      "priority" : 1,
                      "title" : "Learn Quarkus"
                    }
                    """
            ));
    }

    @Test
    @Order(2)
    @EnableEvent("jdk.SocketRead")
    public void createAndRetrieveTodo() throws Exception {
        given()
            .when()
                .contentType(ContentType.JSON)
                .get("/todo/1")
            .then()
                .statusCode(200)
                .body(
                     matchesJson(
                        """
                        {
                          "id" : 1,
                          "completed" : false,
                          "priority" : 1,
                          "title" : "Learn Quarkus"
                        }
                        """
                ));

        jfrEvents.awaitEvents();

//        assertThat(jfrEvents.ofType("org.hibernate.PreparedQuery")).hasSize(1);
    }
}
