package com.example.client;

import com.example.exception.ApiClientException;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@EnableWireMock(@ConfigureWireMock(name = "api", property = "api.base-url"))
class CloudEventClientIntegrationTest {

    @InjectWireMock("api")
    private WireMockServer wireMock;

    @Autowired
    private CloudEventClient cloudEventClient;

    @Test
    void givenServerIsAvailable_whenSendCloudEvent_thenRequestSucceeds() {
        // given
        wireMock.stubFor(post(anyUrl()).willReturn(ok()));

        // when & then
        assertDoesNotThrow(() -> cloudEventClient.sendCloudEvent());
    }

    @Test
    void givenServerReturns500_whenSendCloudEvent_thenThrowsApiClientException() {
        // given
        wireMock.stubFor(post(anyUrl())
                .willReturn(serverError().withBody("Internal Server Error")));

        // when & then
        assertThrows(ApiClientException.class, () -> cloudEventClient.sendCloudEvent());
    }

    @Test
    void givenServerIsAvailable_whenSendCloudEvent_thenRequestContainsCloudEventHeaders() {
        // given
        wireMock.stubFor(post(anyUrl()).willReturn(ok()));

        // when
        cloudEventClient.sendCloudEvent();

        // then
        wireMock.verify(postRequestedFor(anyUrl())
                .withHeader("ce-id", equalTo("4000"))
                .withHeader("ce-type", equalTo("com.example.sending"))
                .withHeader("ce-source", equalTo("/cloud-event-client"))
                .withHeader("ce-specversion", equalTo("1.0"))
                .withHeader("ce-time", matching(".*"))
                .withHeader("Content-Type", containing("application/json"))
                .withRequestBody(equalToJson("{\"id\": 123, \"name\": \"hello\"}")));
    }
}
