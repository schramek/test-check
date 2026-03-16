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
    void givenPayload_whenCreateCloudEvent_thenIdIs4000() {
        // given
        byte[] givenPayload = payload;

        // when
        CloudEvent event = client.createCloudEvent(givenPayload);

        // then
        assertEquals("4000", event.getId());
    }

    @Test
    void givenPayload_whenCreateCloudEvent_thenTypeIsComExampleSending() {
        // given
        byte[] givenPayload = payload;

        // when
        CloudEvent event = client.createCloudEvent(givenPayload);

        // then
        assertEquals("com.example.sending", event.getType());
    }

    @Test
    void givenPayload_whenCreateCloudEvent_thenSourceIsCloudEventClient() {
        // given
        byte[] givenPayload = payload;

        // when
        CloudEvent event = client.createCloudEvent(givenPayload);

        // then
        assertEquals(URI.create("/cloud-event-client"), event.getSource());
    }

    @Test
    void givenPayload_whenCreateCloudEvent_thenSpecVersionIs1() {
        // given
        byte[] givenPayload = payload;

        // when
        CloudEvent event = client.createCloudEvent(givenPayload);

        // then
        assertEquals("1.0", event.getSpecVersion().toString());
    }

    @Test
    void givenPayload_whenCreateCloudEvent_thenTimeIsNow() {
        // given
        OffsetDateTime before = OffsetDateTime.now().minusSeconds(1);

        // when
        CloudEvent event = client.createCloudEvent(payload);

        // then
        assertNotNull(event.getTime());
        assertTrue(event.getTime().isAfter(before));
        assertTrue(event.getTime().isBefore(OffsetDateTime.now().plusSeconds(1)));
    }

    @Test
    void givenPayload_whenCreateCloudEvent_thenDataContentTypeIsJson() {
        // given
        byte[] givenPayload = payload;

        // when
        CloudEvent event = client.createCloudEvent(givenPayload);

        // then
        assertEquals("application/json", event.getDataContentType());
    }

    @Test
    void givenPayload_whenCreateCloudEvent_thenDataMatchesPayload() {
        // given
        byte[] givenPayload = payload;

        // when
        CloudEvent event = client.createCloudEvent(givenPayload);

        // then
        assertNotNull(event.getData());
        String data = new String(event.getData().toBytes(), StandardCharsets.UTF_8);
        assertEquals("{\"id\": 123, \"name\": \"hello\"}", data);
    }
}
