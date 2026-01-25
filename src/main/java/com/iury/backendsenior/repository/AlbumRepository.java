package com.iury.backendsenior.repository;

import com.iury.backendsenior.model.Album;
import com.iury.backendsenior.model.enums.TipoArtista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    Page<Album> findDistinctByArtistas_Tipo(TipoArtista tipo, Pageable pageable);

}
