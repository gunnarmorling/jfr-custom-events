package dev.morling.demos.quarkus;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.Random;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import dev.morling.demos.quarkus.testutil.PostgresResource;
import dev.morling.jfrunit.EnableEvent;
import dev.morling.jfrunit.JfrEvents;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jdk.jfr.consumer.RecordedEvent;

@QuarkusTest
@QuarkusTestResource(PostgresResource.class)
@TestMethodOrder(value = OrderAnnotation.class)
public class TodoResourcePerformanceTest {

    public JfrEvents jfrEvents = new JfrEvents();

    @Test
    @Order(1)
    public void createTodo() {
        Random r = new Random();

        for (int i = 1; i<= 20; i++) {
            given()
            .when()
//                .body(String.format("""
//                      {
//                        "title" : "Learn Quarkus",
//                        "priority" : 1,
//                        "userId" : %s
//                      }
//                      """, r.nextInt(5) + 1))
                .body("""
                    {
                      "title" : "Learn Quarkus",
                      "priority" : 1
                    }
                    """)
                .contentType(ContentType.JSON)
                .post("/todo")
            .then()
                .statusCode(201);
        }
    }

//    @Test
    @Order(2)
    @EnableEvent("jdk.ObjectAllocationInNewTLAB")
    @EnableEvent("jdk.ObjectAllocationOutsideTLAB")
    public void retrieveTodoBaseline() throws Exception {
        Random r = new Random();

        HttpClient client = HttpClient.newBuilder()
                .build();

        for (int i = 1; i<= 100_000; i++) {
            executeRequest(r.nextInt(20) + 1, client);

            if (i % 10_000 == 0) {
                jfrEvents.awaitEvents();

                long sum = jfrEvents.filter(this::isObjectAllocationEvent)
                        .filter(this::isRelevantThread)
                        .mapToLong(this::getAllocationSize)
                        .sum();

                System.out.printf(Locale.ENGLISH, "Requests executed: %s, memory allocated: %s bytes/request%n", i, sum/10_000);
                jfrEvents.reset();
            }
        }
    }

    @Test
    @Order(2)
    @EnableEvent("jdk.ObjectAllocationInNewTLAB")
    @EnableEvent("jdk.ObjectAllocationOutsideTLAB")
//    @EnableConfiguration("profile")
    public void retrieveTodoProfile() throws Exception {
        Random r = new Random();

        HttpClient client = HttpClient.newBuilder()
                .build();

        // warm-up
        for (int i = 1; i<= 20_000; i++) {
            executeRequest(r.nextInt(20) + 1, client);
        }

        jfrEvents.awaitEvents();
        jfrEvents.reset();

        for (int i = 1; i<= 10_000; i++) {
            executeRequest(r.nextInt(20) + 1, client);
        }

        jfrEvents.awaitEvents();

        long sum = jfrEvents.filter(this::isObjectAllocationEvent)
                .filter(this::isRelevantThread)
                .mapToLong(this::getAllocationSize)
                .sum();

        assertThat(sum / 10_000).isLessThan(33_000);
    }

    private void executeRequest(long id, HttpClient client) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8081/todo/" + id))
                .headers("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
    }

    private long getAllocationSize(RecordedEvent re) {
        return re.getEventType().getName().equals("jdk.ObjectAllocationInNewTLAB") ? re.getLong("tlabSize") : re.getLong("allocationSize");
    }

    private boolean isObjectAllocationEvent(RecordedEvent re) {
        return re.getEventType().getName().equals("jdk.ObjectAllocationInNewTLAB") ||
                re.getEventType().getName().equals("jdk.ObjectAllocationOutsideTLAB");
    }

    private boolean isRelevantThread(RecordedEvent re) {
        return re.getThread().getJavaName().startsWith("vert.x-eventloop") ||
                re.getThread().getJavaName().startsWith("executor-thread");
    }
}
