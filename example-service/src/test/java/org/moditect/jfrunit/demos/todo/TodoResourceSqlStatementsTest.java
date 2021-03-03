package org.moditect.jfrunit.demos.todo;

import static dev.morling.jfrunit.EnableEvent.StacktracePolicy.INCLUDED;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Random;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.moditect.jfrunit.demos.todo.testutil.PostgresResource;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import dev.morling.jfrunit.EnableEvent;
import dev.morling.jfrunit.JfrEvents;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@QuarkusTestResource(PostgresResource.class)
@TestMethodOrder(value = OrderAnnotation.class)
public class TodoResourceSqlStatementsTest {

    private static final int ITERATIONS = 10;

    public JfrEvents jfrEvents = new JfrEvents();

    @ConfigProperty(name="jfrunit.database.port")
    public int databasePort;

    @Test
    @Order(1)
    public void setupTodos() {
        Random r = new Random();

        for (int i = 1; i<= 20; i++) {
            given()
                .when()
                    .body(String.format("""
                          {
                            "title" : "Learn Quarkus",
                            "priority" : 1,
                            "userId" : %s
                          }
                          """,
                          r.nextInt(5) + 1)
                    )
                    .contentType(ContentType.JSON)
                    .post("/todo")
                .then()
                    .statusCode(201);
            given()
                .when()
                    .body(String.format("""
                          {
                            "title" : "Learn Quarkus",
                            "priority" : 1,
                            "userId" : %s
                          }
                          """,
                          r.nextInt(5) + 1)
                    )
                    .contentType(ContentType.JSON)
                    .post("/todo/todo-with-details")
                .then()
                    .statusCode(201);
        }
    }

    @Test
    @Order(2)
    public void retrieveTodoBaseline() throws Exception {
        Random r = new Random();

        for (int i = 1; i<= ITERATIONS; i++) {
            int id = r.nextInt(20) + 1;

            given()
                .when()
                    .contentType(ContentType.JSON)
                    .get("/todo/" + id)
                .then()
                    .statusCode(200);
        }

        jfrEvents.awaitEvents();

        jfrEvents.filter(re -> re.getEventType().getName().equals("jdbc.PreparedQuery"))
            .forEach(System.out::println);

        long numberOfStatements = jfrEvents.filter(re -> re.getEventType().getName().equals("jdbc.PreparedQuery"))
            .count();

        System.out.println("### Event count: " + numberOfStatements);
    }

    @Test
    @Order(3)
    public void retrieveTodoShouldYieldCorrectNumberOfSqlStatements() throws Exception {
        Random r = new Random();

        for (int i = 1; i<= ITERATIONS; i++) {
            int id = r.nextInt(20) + 1;

            given()
                .when()
                    .contentType(ContentType.JSON)
                    .get("/todo/" + id)
                .then()
                    .statusCode(200);
        }

        jfrEvents.awaitEvents();

        long numberOfStatements = jfrEvents.filter(re -> re.getEventType().getName().equals("jdbc.PreparedQuery"))
            .count();

        assertThat(numberOfStatements).isEqualTo(ITERATIONS);
    }

    @Test
    @Order(4)
    @EnableEvent(value="jdk.SocketRead", threshold = 0, stackTrace=INCLUDED)
    @EnableEvent(value="jdk.SocketWrite", threshold = 0, stackTrace=INCLUDED)
    public void retrieveTodoSqlStatementRegression() throws Exception {
        Random r = new Random();

        for (int i = 1; i<= ITERATIONS; i++) {
            int id = r.nextInt(20) + 1;

            given()
                .when()
                    .contentType(ContentType.JSON)
                    .get("/todo/with-sql-regression/" + id)
                .then()
                    .statusCode(200);
        }

        jfrEvents.awaitEvents();

        long numberOfStatements = jfrEvents.filter(re -> re.getEventType().getName().equals("jdbc.PreparedQuery"))
            .count();

        // expected to fail
        // assertThat(numberOfStatements).isEqualTo(ITERATIONS);
    }
}
