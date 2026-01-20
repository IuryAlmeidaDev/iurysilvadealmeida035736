package com.iury.backendsenior.controller;

import com.iury.backendsenior.dto.AlbumRequestDTO;
import com.iury.backendsenior.dto.AlbumResponseDTO;
import com.iury.backendsenior.dto.ArtistaDTO;
import com.iury.backendsenior.model.Album;
import com.iury.backendsenior.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/albuns")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService service;

    @PostMapping
    public ResponseEntity<AlbumResponseDTO> criar(@RequestBody AlbumRequestDTO dto) {
        Album album = Album.builder()
                .titulo(dto.titulo())
                .anoLancamento(dto.anoLancamento())
                .build();

        Album salvo = service.salvar(album, dto.artistaIds());

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(salvo));
    }

    @GetMapping
    public ResponseEntity<Page<AlbumResponseDTO>> listar(Pageable pageable) {
        Page<Album> resultado = service.listar(pageable);
        
        List<AlbumResponseDTO> listaDtos = resultado.getContent().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new PageImpl<>(listaDtos, pageable, resultado.getTotalElements()));
    }

    private AlbumResponseDTO toResponseDTO(Album album) {
        List<ArtistaDTO> artistasDto = album.getArtistas().stream()
                .map(a -> new ArtistaDTO(a.getId(), a.getNome()))
                .collect(Collectors.toList());

        return new AlbumResponseDTO(
                album.getId(),
                album.getTitulo(),
                album.getAnoLancamento(),
                album.getCapaUrl(),
                artistasDto
        );
    }
}