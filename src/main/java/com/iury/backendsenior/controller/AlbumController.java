package com.iury.backendsenior.controller;

import com.iury.backendsenior.dto.AlbumRequestDTO;
import com.iury.backendsenior.model.Album;
import com.iury.backendsenior.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/albuns")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService service;

    @PostMapping
    public ResponseEntity<Album> criar(@RequestBody AlbumRequestDTO dto) {
        Album album = Album.builder()
                .titulo(dto.titulo())
                .anoLancamento(dto.anoLancamento())
                .build();

        Album salvo = service.salvar(album, dto.artistaIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public ResponseEntity<Page<Album>> listar(Pageable pageable) {
        return ResponseEntity.ok(service.listar(pageable));
    }
}