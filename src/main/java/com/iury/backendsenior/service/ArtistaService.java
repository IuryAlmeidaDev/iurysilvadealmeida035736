package com.iury.backendsenior.service;

import com.iury.backendsenior.model.Artista;
import com.iury.backendsenior.repository.ArtistaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArtistaService {

    private final ArtistaRepository repository;

    @Transactional
    public Artista salvar(Artista artista) {
        return repository.save(artista);
    }

    public Page<Artista> listar(String termoBusca, Pageable pageable) {
        if (termoBusca != null && !termoBusca.isBlank()) {
            return repository.findByNomeContainingIgnoreCase(termoBusca, pageable);
        }
        return repository.findAll(pageable);
    }

    public Artista buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artista não encontrado com id: " + id));
    }

    @Transactional
    public Artista atualizar(Long id, Artista artistaAtualizado) {
        Artista existente = buscarPorId(id);
        existente.setNome(artistaAtualizado.getNome());
        return repository.save(existente);
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Artista não encontrado com id: " + id);
        }
        repository.deleteById(id);
    }
}