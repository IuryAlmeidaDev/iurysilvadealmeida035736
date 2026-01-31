package com.iury.backendsenior.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class IntegradorClientConfig {

    @Bean
    RestClient integradorRestClient(
            RestClient.Builder builder,
            @Value("${integrador.base-url}") String baseUrl
    ) {
        return builder.baseUrl(baseUrl).build();
    }
}
