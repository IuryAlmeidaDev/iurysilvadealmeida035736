package com.iury.backendsenior.service;

import com.iury.backendsenior.model.Album;
import com.iury.backendsenior.model.enums.TipoArtista;
import com.iury.backendsenior.repository.AlbumRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumRepository repository;

    @InjectMocks
    private AlbumService service;

    @Test
    void deveListarAlbunsSemFiltroQuandoTipoForNulo() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Album> pageMock = new PageImpl<>(List.of());

        when(repository.findAll(pageable)).thenReturn(pageMock);

        Page<Album> resultado = service.listar(pageable, null);

        assertNotNull(resultado);
        verify(repository, times(1)).findAll(pageable);
        verify(repository, never()).findDistinctByArtistas_Tipo(any(), any());
    }

    @Test
    void deveListarAlbunsFiltrandoPorTipoQuandoTipoForInformado() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Album> pageMock = new PageImpl<>(List.of());

        when(repository.findDistinctByArtistas_Tipo(TipoArtista.BANDA, pageable)).thenReturn(pageMock);

        Page<Album> resultado = service.listar(pageable, TipoArtista.BANDA);

        assertNotNull(resultado);
        verify(repository, times(1)).findDistinctByArtistas_Tipo(TipoArtista.BANDA, pageable);
        verify(repository, never()).findAll(any(Pageable.class));
    }
}
