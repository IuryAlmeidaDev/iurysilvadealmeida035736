package com.iury.backendsenior.controller;

import com.iury.backendsenior.model.Artista;
import com.iury.backendsenior.model.enums.TipoArtista;
import com.iury.backendsenior.repository.UsuarioRepository;
import com.iury.backendsenior.service.ArtistaService;
import com.iury.backendsenior.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArtistaController.class)
@AutoConfigureMockMvc(addFilters = false)
class ArtistaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArtistaService service;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deve listar artistas paginados")
    void deveListarArtistas() throws Exception {
        var artista = Artista.builder()
                .id(1L)
                .nome("Artista Teste")
                .tipo(TipoArtista.CANTOR)
                .albuns(Collections.emptyList())
                .build();

        var pageRequest = PageRequest.of(0, 10);
        var pageMock = new PageImpl<>(List.of(artista), pageRequest, 1);

        when(service.listar(eq(null), any(Pageable.class)))
                .thenReturn(pageMock);

        mockMvc.perform(get("/v1/artistas")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nome").value("Artista Teste"))
                .andExpect(jsonPath("$.content[0].tipo").value("CANTOR"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(service, times(1)).listar(eq(null), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve criar artista com sucesso")
    void deveCriarArtistaComSucesso() throws Exception {
        String artistaJson = """
                {
                  "nome": "Novo Artista",
                  "tipo": "BANDA"
                }
                """;

        var artistaSalvo = Artista.builder()
                .id(1L)
                .nome("Novo Artista")
                .tipo(TipoArtista.BANDA)
                .albuns(Collections.emptyList())
                .build();

        when(service.salvar(any(Artista.class)))
                .thenReturn(artistaSalvo);

        mockMvc.perform(post("/v1/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(artistaJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Novo Artista"))
                .andExpect(jsonPath("$.tipo").value("BANDA"));

        verify(service, times(1)).salvar(any(Artista.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao tentar criar artista inválido")
    void deveRetornarBadRequestQuandoCriarArtistaInvalido() throws Exception {
        String artistaJson = """
                {
                  "nome": ""
                }
                """;

        mockMvc.perform(post("/v1/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(artistaJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}
