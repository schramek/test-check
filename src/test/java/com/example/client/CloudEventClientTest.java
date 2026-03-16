package com.example.client;

import com.example.config.RestClientConfig;
import com.example.exception.ApiClientException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(CloudEventClient.class)
@Import(RestClientConfig.class)
@TestPropertySource(properties = "api.base-url=http://localhost:3000/api")
class CloudEventClientTest {

    @Autowired
    private CloudEventClient cloudEventClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Test
    void givenServerReturns200_whenSendCloudEvent_thenRequestContainsCorrectHeadersAndBody() {
        // given
        mockServer.expect(requestTo("http://localhost:3000/api"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("ce-id", "4000"))
                .andExpect(header("ce-type", "com.example.sending"))
                .andExpect(header("ce-source", "/cloud-event-client"))
                .andExpect(header("ce-specversion", "1.0"))
                .andExpect(header("ce-time", not(emptyOrNullString())))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"id\": 123, \"name\": \"hello\"}"))
                .andRespond(withSuccess());

        // when & then
        assertDoesNotThrow(() -> cloudEventClient.sendCloudEvent());
        mockServer.verify();
    }

    @Test
    void givenServerReturns201_whenSendCloudEvent_thenNoExceptionIsThrown() {
        // given
        mockServer.expect(requestTo("http://localhost:3000/api"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withCreatedEntity(null));

        // when & then
        assertDoesNotThrow(() -> cloudEventClient.sendCloudEvent());
        mockServer.verify();
    }

    @Test
    void givenServerReturns202_whenSendCloudEvent_thenNoExceptionIsThrown() {
        // given
        mockServer.expect(requestTo("http://localhost:3000/api"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withAccepted());

        // when & then
        assertDoesNotThrow(() -> cloudEventClient.sendCloudEvent());
        mockServer.verify();
    }

    @Test
    void givenServerReturns400_whenSendCloudEvent_thenThrowsApiClientException() {
        // given
        mockServer.expect(requestTo("http://localhost:3000/api"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest().body("Bad Request"));

        // when
        ApiClientException exception = assertThrows(
                ApiClientException.class,
                () -> cloudEventClient.sendCloudEvent());

        // then
        assertEquals(400, exception.getStatusCode().value());
        assertEquals("Bad Request", exception.getResponseBody());
    }

    @Test
    void givenServerReturns500_whenSendCloudEvent_thenThrowsApiClientException() {
        // given
        mockServer.expect(requestTo("http://localhost:3000/api"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError().body("Internal Server Error"));

        // when
        ApiClientException exception = assertThrows(
                ApiClientException.class,
                () -> cloudEventClient.sendCloudEvent());

        // then
        assertEquals(500, exception.getStatusCode().value());
        assertEquals("Internal Server Error", exception.getResponseBody());
    }
}
