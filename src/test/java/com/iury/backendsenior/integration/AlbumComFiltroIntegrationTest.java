package com.iury.backendsenior.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iury.backendsenior.config.IntegrationTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
class AlbumComFiltroIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve criar álbum com artista e listar filtrando por tipoArtista (H2 real)")
    void deveCriarAlbumEListarComFiltroTipoArtista() throws Exception {
        String artistaJson = """
                {
                  "nome": "Banda Integração",
                  "tipo": "BANDA"
                }
                """;

        var artistaResp = mockMvc.perform(post("/v1/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(artistaJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.tipo").value("BANDA"))
                .andReturn();

        Long artistaId = objectMapper.readTree(artistaResp.getResponse().getContentAsString())
                .get("id").asLong();

        String albumJson = """
                {
                  "titulo": "Album Integração Filtro",
                  "anoLancamento": 2024,
                  "artistaIds": [%d]
                }
                """.formatted(artistaId);

        mockMvc.perform(post("/v1/albuns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(albumJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.titulo").value("Album Integração Filtro"))
                .andExpect(jsonPath("$.artistas").isArray())
                .andExpect(jsonPath("$.artistas[0].id").value(artistaId))
                .andExpect(jsonPath("$.artistas[0].tipo").value("BANDA"));

        mockMvc.perform(get("/v1/albuns")
                        .param("tipoArtista", "BANDA")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].titulo").value("Album Integração Filtro"))
                .andExpect(jsonPath("$.content[0].artistas[0].tipo").value("BANDA"));
    }
}
