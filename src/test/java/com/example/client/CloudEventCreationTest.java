package com.example.client;

import io.cloudevents.CloudEvent;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CloudEventCreationTest {

    private final CloudEventClient client = new CloudEventClient(null);

    private final byte[] payload = "{\"id\": 123, \"name\": \"hello\"}".getBytes(StandardCharsets.UTF_8);

    @Test
    void createCloudEvent_setsIdCorrectly() {
        CloudEvent event = client.createCloudEvent(payload);

        assertEquals("4000", event.getId());
    }

    @Test
    void createCloudEvent_setsTypeCorrectly() {
        CloudEvent event = client.createCloudEvent(payload);

        assertEquals("com.example.sending", event.getType());
    }

    @Test
    void createCloudEvent_setsSourceCorrectly() {
        CloudEvent event = client.createCloudEvent(payload);

        assertEquals(URI.create("/cloud-event-client"), event.getSource());
    }

    @Test
    void createCloudEvent_setsSpecVersion() {
        CloudEvent event = client.createCloudEvent(payload);

        assertEquals("1.0", event.getSpecVersion().toString());
    }

    @Test
    void createCloudEvent_setsTimeToNow() {
        OffsetDateTime before = OffsetDateTime.now().minusSeconds(1);

        CloudEvent event = client.createCloudEvent(payload);

        assertNotNull(event.getTime());
        assertTrue(event.getTime().isAfter(before));
        assertTrue(event.getTime().isBefore(OffsetDateTime.now().plusSeconds(1)));
    }

    @Test
    void createCloudEvent_setsDataContentType() {
        CloudEvent event = client.createCloudEvent(payload);

        assertEquals("application/json", event.getDataContentType());
    }

    @Test
    void createCloudEvent_setsPayloadData() {
        CloudEvent event = client.createCloudEvent(payload);

        assertNotNull(event.getData());
        String data = new String(event.getData().toBytes(), StandardCharsets.UTF_8);
        assertEquals("{\"id\": 123, \"name\": \"hello\"}", data);
    }
}
