package com.iury.backendsenior.service;

import com.iury.backendsenior.model.Artista;
import com.iury.backendsenior.repository.ArtistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArtistaService {

    private final ArtistaRepository repository;

    @Transactional
    public Artista salvar(Artista artista) {
        return repository.save(artista);
    }

    // Requisito: "Consultas por nome do artista com ordenação alfabética"
    // A ordenação virá dentro do objeto 'Pageable' enviado pelo Controller
    public Page<Artista> listar(String filtroNome, Pageable pageable) {
        if (filtroNome != null && !filtroNome.isBlank()) {
            return repository.findByNomeContainingIgnoreCase(filtroNome, pageable);
        }
        return repository.findAll(pageable);
    }

    public Optional<Artista> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}