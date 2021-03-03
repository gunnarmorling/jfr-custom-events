package org.moditect.jfrunit.demos.todo.testutil;

import java.util.HashMap;
import java.util.Map;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class PostgresResource implements QuarkusTestResourceLifecycleManager {

    static PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("tododb")
            .withUsername("todouser")
            .withPassword("todopw")
            .withReuse(true)
            .withClasspathResourceMapping("init.sql",
                    "/docker-entrypoint-initdb.d/init.sql",
                    BindMode.READ_ONLY);

    @Override
    public Map<String, String> start() {
        db.start();

        Map<String, String> props = new HashMap<>();
        props.put("quarkus.datasource.jdbc.url", db.getJdbcUrl());
        props.put("jfrunit.database.port", String.valueOf(db.getFirstMappedPort()));

        return props;
    }

    @Override
    public void stop() {
        db.stop();
    }
}
