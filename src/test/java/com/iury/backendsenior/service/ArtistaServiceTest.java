package com.iury.backendsenior.service;

import com.iury.backendsenior.model.Artista;
import com.iury.backendsenior.repository.ArtistaRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistaServiceTest {

    @Mock
    private ArtistaRepository repository;

    @InjectMocks
    private ArtistaService service;

    @Test
    void deveListarComBuscaQuandoTermoForInformado() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Artista> pageMock = new PageImpl<>(List.of());

        when(repository.findByNomeContainingIgnoreCase("iu", pageable)).thenReturn(pageMock);

        Page<Artista> resultado = service.listar("iu", pageable);

        assertNotNull(resultado);
        verify(repository, times(1)).findByNomeContainingIgnoreCase("iu", pageable);
        verify(repository, never()).findAll(any(Pageable.class));
    }

    @Test
    void deveListarSemBuscaQuandoTermoForNuloOuVazio() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Artista> pageMock = new PageImpl<>(List.of());

        when(repository.findAll(pageable)).thenReturn(pageMock);

        Page<Artista> resultado = service.listar(null, pageable);

        assertNotNull(resultado);
        verify(repository, times(1)).findAll(pageable);
        verify(repository, never()).findByNomeContainingIgnoreCase(anyString(), any(Pageable.class));
    }
}
