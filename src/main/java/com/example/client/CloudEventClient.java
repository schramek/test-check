package com.example.client;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.spring.http.CloudEventHttpUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

@Service
public class CloudEventClient {

    private final RestClient restClient;

    public CloudEventClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public void sendCloudEvent() {
        byte[] payload = "{\"id\": 123, \"name\": \"hello\"}".getBytes(StandardCharsets.UTF_8);

        CloudEvent event = CloudEventBuilder.v1()
                .withId("4000")
                .withType("com.example.sending")
                .withSource(URI.create("/cloud-event-client"))
                .withTime(OffsetDateTime.now())
                .withDataContentType("application/json")
                .withData("application/json", payload)
                .build();

        HttpHeaders ceHeaders = CloudEventHttpUtils.toHttp(event);

        restClient.post()
                .headers(h -> h.addAll(ceHeaders))
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }
}
