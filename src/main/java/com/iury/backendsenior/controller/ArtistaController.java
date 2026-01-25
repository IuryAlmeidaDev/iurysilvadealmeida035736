package com.iury.backendsenior.controller;

import com.iury.backendsenior.dto.ArtistaDTO;
import com.iury.backendsenior.model.Artista;
import com.iury.backendsenior.service.ArtistaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/artistas")
@RequiredArgsConstructor
public class ArtistaController {

    private final ArtistaService service;

    @PostMapping
    public ResponseEntity<ArtistaDTO> criar(@RequestBody @Valid ArtistaDTO dto) {
        Artista artista = new Artista();
        artista.setNome(dto.nome());
        artista.setTipo(dto.tipo());

        Artista salvo = service.salvar(artista);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(salvo));
    }

    @GetMapping
    public ResponseEntity<Page<ArtistaDTO>> listar(
            @RequestParam(required = false) String busca,
            @PageableDefault(sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<Artista> pagina = service.listar(busca, pageable);
        return ResponseEntity.ok(pagina.map(this::toDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistaDTO> buscarPorId(@PathVariable Long id) {
        Artista artista = service.buscarPorId(id);
        return ResponseEntity.ok(toDTO(artista));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistaDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ArtistaDTO dto
    ) {
        Artista artista = new Artista();
        artista.setNome(dto.nome());
        artista.setTipo(dto.tipo());

        Artista atualizado = service.atualizar(id, artista);
        return ResponseEntity.ok(toDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private ArtistaDTO toDTO(Artista artista) {
        return new ArtistaDTO(
                artista.getId(),
                artista.getNome(),
                artista.getTipo()
        );
    }
}
