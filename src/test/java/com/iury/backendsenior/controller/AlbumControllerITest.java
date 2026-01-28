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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        when(minioService.gerarUrlPreAssinada(anyString())).thenReturn("http://teste-url");

        var album = Album.builder()
                .id(1L)
                .titulo("Album Teste")
                .anoLancamento(2024)
                .artistas(Collections.emptyList())
                .imagens(Collections.emptyList())
                .build();

        var pageRequest = PageRequest.of(0, 10);
        var pageMock = new PageImpl<>(List.of(album), pageRequest, 1);

        when(service.listar(any(Pageable.class), isNull())).thenReturn(pageMock);

        mockMvc.perform(get("/v1/albuns")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].titulo").value("Album Teste"))
                .andExpect(jsonPath("$.content[0].anoLancamento").value(2024))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(service, times(1)).listar(any(Pageable.class), isNull());
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("Deve filtrar álbuns por tipo de artista corretamente")
    void deveListarAlbunsComFiltroTipoArtista() throws Exception {
        when(minioService.gerarUrlPreAssinada(anyString())).thenReturn("http://teste-url");

        var pageMock = new PageImpl<Album>(List.of(), PageRequest.of(0, 10), 0);

        when(service.listar(any(Pageable.class), eq(TipoArtista.BANDA))).thenReturn(pageMock);

        mockMvc.perform(get("/v1/albuns")
                        .param("tipoArtista", "BANDA")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service, times(1)).listar(any(Pageable.class), eq(TipoArtista.BANDA));
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o tipo de artista for inválido")
    void deveRetornarBadRequestQuandoTipoArtistaInvalido() throws Exception {
        mockMvc.perform(get("/v1/albuns")
                        .param("tipoArtista", "INVALIDO")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Deve salvar um novo álbum com sucesso")
    void deveSalvarAlbumComSucesso() throws Exception {
        when(minioService.gerarUrlPreAssinada(anyString())).thenReturn("http://teste-url");

        String albumJson = """
                {
                  "titulo": "Novo Álbum",
                  "anoLancamento": 2024,
                  "artistaIds": [1]
                }
                """;

        var albumSalvo = Album.builder()
                .id(1L)
                .titulo("Novo Álbum")
                .anoLancamento(2024)
                .artistas(Collections.emptyList())
                .imagens(Collections.emptyList())
                .build();

        when(service.salvar(any(Album.class), anyList())).thenReturn(albumSalvo);

        mockMvc.perform(post("/v1/albuns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(albumJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Novo Álbum"))
                .andExpect(jsonPath("$.anoLancamento").value(2024));

        verify(service, times(1)).salvar(any(Album.class), anyList());
        verifyNoMoreInteractions(service);
    }
}
