package com.iury.backendsenior.service;

import com.iury.backendsenior.dto.regional.RegionalIntegradorDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RegionalIntegradorClient {

    private final RestClient integradorRestClient;

    public List<RegionalIntegradorDTO> buscarRegionais() {
        RegionalIntegradorDTO[] response = integradorRestClient
                .get()
                .uri("/v1/regionais")
                .retrieve()
                .body(RegionalIntegradorDTO[].class);

        if (response == null) return List.of();
        return Arrays.asList(response);
    }
}
