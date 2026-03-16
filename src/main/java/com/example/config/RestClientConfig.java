package com.example.config;

import com.example.exception.ApiClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Set;

@Configuration
public class RestClientConfig {

    private static final Set<Integer> ACCEPTED_STATUS_CODES = Set.of(200, 201, 202);

    @Value("${api.base-url}")
    private String apiBaseUrl;

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder
                .baseUrl(apiBaseUrl)
                .defaultStatusHandler(
                        status -> !ACCEPTED_STATUS_CODES.contains(status.value()),
                        (request, response) -> {
                            String body = new String(
                                    response.getBody().readAllBytes(), StandardCharsets.UTF_8);
                            throw new ApiClientException(
                                    "Unexpected status: " + response.getStatusCode(),
                                    response.getStatusCode(),
                                    body);
                        })
                .build();
    }
}
