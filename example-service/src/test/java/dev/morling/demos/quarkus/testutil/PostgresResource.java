package dev.morling.demos.quarkus.testutil;

import java.util.Collections;
import java.util.Map;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class PostgresResource implements QuarkusTestResourceLifecycleManager {

    static PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("tododb")
            .withUsername("todouser")
            .withPassword("todopw")
            .withClasspathResourceMapping("init.sql",
                    "/docker-entrypoint-initdb.d/init.sql",
                    BindMode.READ_ONLY);

    @Override
    public Map<String, String> start() {
        db.start();
        return Collections.singletonMap("quarkus.datasource.url", db.getJdbcUrl());
    }

    @Override
    public void stop() {
        db.close();
    }
}
