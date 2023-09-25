package ua.lviv.javaclub.metricsdemo;

import com.codahale.metrics.health.HealthCheck;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static ua.lviv.javaclub.metricsdemo.DemoController.ALIVE;

@Slf4j
public class DemoHealthCheck extends HealthCheck {

    private final URL url;

    public DemoHealthCheck() throws IOException {
        url = new URL("http://localhost:8080/healthCheck");
    }

    private String queryHealthResponse() {
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(2000);
            con.setReadTimeout(3000);
            con.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String response = in.readLine();
            in.close();
            con.disconnect();
            return response;
        } catch (IOException e) {
            String message = e.getMessage();
            log.error(message);
            return message;
        }
    }

    @Override
    protected Result check() {
        String healthCheckMessage = queryHealthResponse();
        return ALIVE.equals(healthCheckMessage) ? Result.healthy() : Result.unhealthy(healthCheckMessage);
    }
}
