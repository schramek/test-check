package com.example.model;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class PayloadTest {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Test
    void givenIdAndName_whenCreated_thenAccessorsReturnCorrectValues() {
        // given
        int id = 123;
        String name = "hello";

        // when
        Payload payload = new Payload(id, name);

        // then
        assertEquals(123, payload.id());
        assertEquals("hello", payload.name());
    }

    @Test
    void givenTwoEqualPayloads_whenCompared_thenTheyAreEqual() {
        // given
        Payload first = new Payload(123, "hello");
        Payload second = new Payload(123, "hello");

        // when & then
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void givenTwoDifferentPayloads_whenCompared_thenTheyAreNotEqual() {
        // given
        Payload first = new Payload(123, "hello");
        Payload second = new Payload(456, "world");

        // when & then
        assertNotEquals(first, second);
    }

    @Test
    void givenPayload_whenSerialized_thenJsonContainsIdAndName() {
        // given
        Payload payload = new Payload(123, "hello");

        // when
        byte[] json = jsonMapper.writeValueAsBytes(payload);

        // then
        String jsonString = new String(json, StandardCharsets.UTF_8);
        assertEquals("{\"id\":123,\"name\":\"hello\"}", jsonString);
    }

    @Test
    void givenJson_whenDeserialized_thenPayloadIsCorrect() {
        // given
        String json = "{\"id\":123,\"name\":\"hello\"}";

        // when
        Payload payload = jsonMapper.readValue(json, Payload.class);

        // then
        assertEquals(123, payload.id());
        assertEquals("hello", payload.name());
    }
}
