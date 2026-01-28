package com.iury.backendsenior.controller;

import com.iury.backendsenior.model.Album;
import com.iury.backendsenior.model.enums.TipoArtista;
import com.iury.backendsenior.repository.UsuarioRepository;
import com.iury.backendsenior.service.AlbumService;
import com.iury.backendsenior.service.MinioService;
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
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlbumController.class)
@AutoConfigureMockMvc(addFilters = false)
class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlbumService service;

    @MockBean
    private MinioService minioService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deve retornar lista de álbuns paginada quando não houver filtros")
    void deveListarAlbunsSemFiltro() throws Exception {
        // Arrange
        // IMPORTANTE: Como não mudamos a model, precisamos passar List.of() explicitamente 
        // para evitar o NullPointerException no Controller/Mapper
        var album = Album.builder()
                .id(1L)
                .titulo("Album Teste")
                .anoLancamento(2024)
                .artistas(Collections.emptyList()) // Solução para o erro de stream()
                .imagens(Collections.emptyList())  // Prevenção para outras possíveis listas
                .build();

        var pageRequest = PageRequest.of(0, 10);
        var pageMock = new PageImpl<>(List.of(album), pageRequest, 1);

        when(service.listar(any(Pageable.class), isNull())).thenReturn(pageMock);

        // Act & Assert
        mockMvc.perform(get("/v1/albuns")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].titulo").value("Album Teste"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(service, times(1)).listar(any(Pageable.class), isNull());
    }

    @Test
    @DisplayName("Deve filtrar álbuns por tipo de artista corretamente")
    void deveListarAlbunsComFiltroTipoArtista() throws Exception {
        var pageMock = new PageImpl<Album>(List.of());
        when(service.listar(any(Pageable.class), eq(TipoArtista.BANDA))).thenReturn(pageMock);

        mockMvc.perform(get("/v1/albuns")
                        .param("tipoArtista", "BANDA"))
                .andExpect(status().isOk());

        verify(service).listar(any(Pageable.class), eq(TipoArtista.BANDA));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o tipo de artista for inválido")
    void deveRetornarBadRequestQuandoTipoArtistaInvalido() throws Exception {
        mockMvc.perform(get("/v1/albuns")
                        .param("tipoArtista", "INVALIDO"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}