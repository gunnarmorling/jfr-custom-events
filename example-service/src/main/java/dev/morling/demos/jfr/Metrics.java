package dev.morling.demos.jfr;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jdk.jfr.consumer.RecordingStream;

@ApplicationScoped
public class Metrics {

    private RecordingStream rs;

    @Inject
    MetricRegistry metricsRegistry;

    public void onStartup(@Observes StartupEvent se) {
        rs = new RecordingStream();
        rs.enable(JaxRsInvocationEvent.NAME);

        rs.onEvent(JaxRsInvocationEvent.NAME, event -> {

            String path = event.getString("path").replaceAll("(\\/)([0-9]+)(\\/?)", "$1{param}$3");
            String method = event.getString("method");
            String name = path + "-" + method;

            Metadata metadata = metricsRegistry.getMetadata().get(name);
            if (metadata == null) {
                metricsRegistry.timer(Metadata.builder()
                        .withName(name)
                        .withType(MetricType.TIMER)
                        .withDescription("Metrics for " + path + " (" + method + ")")
                        .build()).update(event.getDuration().toNanos(), TimeUnit.NANOSECONDS);
            }
            else {
                metricsRegistry.timer(name).update(event.getDuration().toNanos(), TimeUnit.NANOSECONDS);
            }
        });
        rs.startAsync();
    }

    public void stop(@Observes ShutdownEvent se) {
        rs.close();
        try {
            rs.awaitTermination();
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
