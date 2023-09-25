package ua.lviv.javaclub.metricsdemo;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Collectors;

import static com.codahale.metrics.MetricRegistry.name;

@Component
public class Watcher {

    private final HealthCheckRegistry healthChecks;
    private final Counter microCounter;

    public Watcher(MetricRegistry metrics, HealthCheckRegistry healthCheckRegistry, MeterRegistry meterRegistry) {
        this.healthChecks = healthCheckRegistry;
        metrics.register(name(Watcher.class, "JVM", "free-memory-size"),
                (Gauge<Long>) () -> Runtime.getRuntime().freeMemory());

        try {
            healthChecks.register("controller-health", new DemoHealthCheck());
        } catch (IOException ignored) {
        }

        microCounter = Counter.builder("watcher")
                .description("Micrometer counter for Watcher")
                .register(meterRegistry);
    }

    public String queryHealthCheck() {
        microCounter.increment();
        return healthChecks.runHealthChecks().entrySet().stream()
                .map(e -> String.join("\t", e.getKey(), e.getValue().isHealthy() ? "alive" : "dead"))
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
