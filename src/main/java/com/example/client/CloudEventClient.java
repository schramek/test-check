package com.example.client;

import com.example.model.Payload;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.spring.http.CloudEventHttpUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.time.OffsetDateTime;

@Service
public class CloudEventClient {

    private final RestClient restClient;
    private final JsonMapper jsonMapper;

    public CloudEventClient(RestClient restClient, JsonMapper jsonMapper) {
        this.restClient = restClient;
        this.jsonMapper = jsonMapper;
    }

    public void sendCloudEvent() {
        Payload payload = new Payload(123, "hello");
        byte[] payloadBytes = jsonMapper.writeValueAsBytes(payload);
        CloudEvent event = createCloudEvent(payloadBytes);
        HttpHeaders ceHeaders = CloudEventHttpUtils.toHttp(event);

        restClient.post()
                .headers(h -> h.addAll(ceHeaders))
                .contentType(MediaType.APPLICATION_JSON)
                .body(payloadBytes)
                .retrieve()
                .toBodilessEntity();
    }

    CloudEvent createCloudEvent(byte[] payload) {
        return CloudEventBuilder.v1()
                .withId("4000")
                .withType("com.example.sending")
                .withSource(URI.create("/cloud-event-client"))
                .withTime(OffsetDateTime.now())
                .withDataContentType("application/json")
                .withData("application/json", payload)
                .build();
    }
}
