package com.iury.backendsenior.service;

import com.iury.backendsenior.model.Artista;
import com.iury.backendsenior.model.enums.TipoArtista;
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
        // Segurança extra: se vier null do request, seta default
        if (artista.getTipo() == null) {
            artista.setTipo(TipoArtista.CANTOR);
        }
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

        if (artistaAtualizado.getNome() != null && !artistaAtualizado.getNome().isBlank()) {
            existente.setNome(artistaAtualizado.getNome());
        }

        // Se não vier tipo, mantém o atual (evita trocar por null)
        if (artistaAtualizado.getTipo() != null) {
            existente.setTipo(artistaAtualizado.getTipo());
        }

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
