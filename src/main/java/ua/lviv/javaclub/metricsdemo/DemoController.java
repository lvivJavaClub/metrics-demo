package ua.lviv.javaclub.metricsdemo;

import com.codahale.metrics.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

import static com.codahale.metrics.MetricRegistry.name;

@RestController
@Slf4j
public class DemoController {

    public static final String ALIVE = "Alive!";

    // Metrics
    private final Meter requests;
    private final Counter visitors, total;
    private final Histogram nameSizes;
    private final Timer responses;

    private final Random random;

    @Autowired
    private Watcher watcher;

    public DemoController(final MetricRegistry metrics) {
        requests = metrics.meter(name(DemoController.class, "requests"));
        total = metrics.counter("visitors.total");
        visitors = metrics.counter("visitors.current");
        nameSizes = metrics.histogram("name-sizes");
        responses = metrics.timer(name(DemoController.class, "responses"));
        random = new Random();
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        requests.mark();
        total.inc();
        visitors.inc();
        nameSizes.update(name.length());

        try (final Timer.Context context = responses.time()) {
            try {
                Thread.sleep(random.nextLong(1000));
            } catch (InterruptedException ignored) {
            }
            return String.format("%d. Hello %s! Application health status: %s",
                    total.getCount(), name, watcher.queryHealthCheck());
        }
    }

    @GetMapping("/bye")
    public String bye() {
        total.inc();
        if (visitors.getCount() > 0) {
            visitors.dec();
        }

        return "Bye!";
    }

    @GetMapping("/healthCheck")
    public String healthCheck() { return ALIVE; }

}
