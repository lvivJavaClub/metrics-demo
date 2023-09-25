package ua.lviv.javaclub.metricsdemo;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.health.HealthCheckRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ReportersConfiguration {

    public ReportersConfiguration(MetricRegistry metricRegistry) {
        // Reporters
        final Slf4jReporter slf4jMetricsReporter = Slf4jReporter.forRegistry(metricRegistry)
                .outputTo(log)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        slf4jMetricsReporter.start(1, TimeUnit.MINUTES);

        final Graphite graphite = new Graphite(new InetSocketAddress("localhost", 2003));
        final GraphiteReporter graphiteMetricsReporter = GraphiteReporter.forRegistry(metricRegistry)
                .prefixedWith("ua.lviv.javaclub")
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(graphite);
        graphiteMetricsReporter.start(5, TimeUnit.SECONDS);
    }

}
