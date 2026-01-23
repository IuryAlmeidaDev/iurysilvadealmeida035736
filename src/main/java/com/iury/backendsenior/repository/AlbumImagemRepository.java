package com.iury.backendsenior.repository;

import com.iury.backendsenior.model.AlbumImagem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumImagemRepository extends JpaRepository<AlbumImagem, Long> {
    List<AlbumImagem> findByAlbumIdOrderByOrdemAsc(Long albumId);
}
