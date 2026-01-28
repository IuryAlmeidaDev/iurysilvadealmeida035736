package com.iury.backendsenior.controller;

import com.iury.backendsenior.model.Album;
import com.iury.backendsenior.model.enums.TipoArtista;
import com.iury.backendsenior.repository.UsuarioRepository;
import com.iury.backendsenior.service.AlbumService;
import com.iury.backendsenior.service.MinioService;
import com.iury.backendsenior.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AlbumController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "JWT_SECRET=teste",
        "JWT_EXPIRATION=300000",
        "JWT_REFRESH_EXPIRATION=2592000000",
        "MINIO_URL=http://localhost:9000",
        "MINIO_ACCESS_KEY=minioadmin",
        "MINIO_SECRET_KEY=minioadmin",
        "MINIO_BUCKET=capas-albuns",
        "APP_CORS_ALLOWED_ORIGINS=http://localhost:3000"
})
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
    void deveListarAlbunsSemFiltro() throws Exception {
        Page<Album> pageMock = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        when(service.listar(any(), isNull())).thenReturn(pageMock);

        mockMvc.perform(get("/v1/albuns"))
                .andExpect(status().isOk());

        verify(service, times(1)).listar(any(), isNull());
        verifyNoMoreInteractions(service);
    }

    @Test
    void deveListarAlbunsComFiltroTipoArtista() throws Exception {
        Page<Album> pageMock = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        when(service.listar(any(), eq(TipoArtista.BANDA))).thenReturn(pageMock);

        mockMvc.perform(get("/v1/albuns")
                        .param("tipoArtista", "BANDA"))
                .andExpect(status().isOk());

        verify(service, times(1)).listar(any(), eq(TipoArtista.BANDA));
        verifyNoMoreInteractions(service);
    }

    @Test
    void deveRetornarBadRequestQuandoTipoArtistaInvalido() throws Exception {
        mockMvc.perform(get("/v1/albuns")
                        .param("tipoArtista", "INVALIDO"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}
