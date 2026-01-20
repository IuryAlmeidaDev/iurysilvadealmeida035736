package com.iury.backendsenior.repository;

import com.iury.backendsenior.model.Artista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistaRepository extends JpaRepository<Artista, Long> {
    Page<Artista> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}