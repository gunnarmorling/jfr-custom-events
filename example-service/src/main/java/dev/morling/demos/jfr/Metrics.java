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
import jdk.jfr.FlightRecorder;
import jdk.jfr.consumer.RecordingStream;

@ApplicationScoped
public class Metrics {

    private RecordingStream recordingStream;

    @Inject
    MetricRegistry metricsRegistry;

    public void registerEvent(@Observes StartupEvent se) {
        FlightRecorder.register(JaxRsInvocationEvent.class);
        System.out.println("#### Registered");
    }

    public void onStartup(@Observes StartupEvent se) {
        recordingStream = new RecordingStream();
        recordingStream.enable(JaxRsInvocationEvent.NAME);

        recordingStream.onEvent(JaxRsInvocationEvent.NAME, event -> {

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
        recordingStream.startAsync();
    }

    public void stop(@Observes ShutdownEvent se) {
        recordingStream.close();
        try {
            recordingStream.awaitTermination();
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
