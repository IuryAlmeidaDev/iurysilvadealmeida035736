package com.iury.backendsenior.controller;

import com.iury.backendsenior.dto.ArtistaDTO;
import com.iury.backendsenior.model.Artista;
import com.iury.backendsenior.service.ArtistaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/artistas")
@RequiredArgsConstructor
public class ArtistaController {

    private final ArtistaService service;

    @PostMapping
    public ResponseEntity<Artista> criar(@RequestBody @Valid ArtistaDTO dto) {
        Artista artistaParaSalvar = Artista.builder()
                .nome(dto.nome())
                .build();

        Artista salvo = service.salvar(artistaParaSalvar);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public ResponseEntity<Page<Artista>> listar(
            @RequestParam(required = false) String nome,
            Pageable pageable) {
        return ResponseEntity.ok(service.listar(nome, pageable));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}