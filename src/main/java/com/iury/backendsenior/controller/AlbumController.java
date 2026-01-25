package com.iury.backendsenior.controller;

import com.iury.backendsenior.dto.AlbumRequestDTO;
import com.iury.backendsenior.dto.AlbumResponseDTO;
import com.iury.backendsenior.dto.ArtistaDTO;
import com.iury.backendsenior.model.Album;
import com.iury.backendsenior.model.enums.TipoArtista;
import com.iury.backendsenior.service.AlbumService;
import com.iury.backendsenior.service.MinioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/albuns")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService service;
    private final MinioService minioService;

    @PostMapping
    public ResponseEntity<AlbumResponseDTO> criar(@RequestBody @Valid AlbumRequestDTO dto) {
        Album album = Album.builder()
                .titulo(dto.titulo())
                .anoLancamento(dto.anoLancamento())
                .build();

        Album salvo = service.salvar(album, dto.artistaIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(salvo));
    }

    @PostMapping(value = "/{id}/capa", consumes = "multipart/form-data")
    public ResponseEntity<AlbumResponseDTO> uploadCapa(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        Album albumAtualizado = service.atualizarCapa(id, file);
        return ResponseEntity.ok(toResponseDTO(albumAtualizado));
    }

    @PostMapping(value = "/{id}/imagens", consumes = "multipart/form-data")
    public ResponseEntity<List<String>> uploadImagens(
            @PathVariable Long id,
            @RequestParam("files") MultipartFile[] files
    ) {
        List<String> urls = service.adicionarImagens(id, files);
        return ResponseEntity.ok(urls);
    }

    @GetMapping
    public ResponseEntity<Page<AlbumResponseDTO>> listar(
            @RequestParam(required = false) TipoArtista tipoArtista,
            Pageable pageable
    ) {
        Page<Album> resultado = service.listar(pageable, tipoArtista);

        List<AlbumResponseDTO> listaDtos = resultado.getContent().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new PageImpl<>(listaDtos, pageable, resultado.getTotalElements()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDTO> buscarPorId(@PathVariable Long id) {
        Album album = service.buscarPorId(id);
        return ResponseEntity.ok(toResponseDTO(album));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid AlbumRequestDTO dto
    ) {
        Album album = Album.builder()
                .titulo(dto.titulo())
                .anoLancamento(dto.anoLancamento())
                .build();

        Album atualizado = service.atualizar(id, album, dto.artistaIds());
        return ResponseEntity.ok(toResponseDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private AlbumResponseDTO toResponseDTO(Album album) {
        List<ArtistaDTO> artistasDto = album.getArtistas().stream()
                .map(a -> new ArtistaDTO(a.getId(), a.getNome(), a.getTipo()))
                .collect(Collectors.toList());

        String capaUrl = null;
        if (album.getCapaUrl() != null && !album.getCapaUrl().isBlank()) {
            capaUrl = minioService.gerarUrlPreAssinada(album.getCapaUrl());
        }

        List<String> imagens = album.getImagens().stream()
                .map(img -> minioService.gerarUrlPreAssinada(img.getNomeArquivo()))
                .toList();

        return new AlbumResponseDTO(
                album.getId(),
                album.getTitulo(),
                album.getAnoLancamento(),
                capaUrl,
                imagens,
                artistasDto
        );
    }
}
