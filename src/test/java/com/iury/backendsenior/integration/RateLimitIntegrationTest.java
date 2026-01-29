package com.iury.backendsenior.integration;

import com.iury.backendsenior.config.IntegrationTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class RateLimitIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser // Simula um usuário autenticado para evitar o 403
    @DisplayName("Deve retornar 429 após 10 requisições")
    void deveAplicarRateLimit() throws Exception {
        String url = "/v1/albuns"; 

        // 10 requisições permitidas (Status 200)
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(get(url))
                    .andExpect(status().isOk());
        }

        // A 11ª deve retornar 429
        mockMvc.perform(get(url))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("X-Rate-Limit-Remaining", "0"));
    }
}