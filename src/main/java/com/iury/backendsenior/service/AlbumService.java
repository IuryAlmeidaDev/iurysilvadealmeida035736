package com.iury.backendsenior.service;

import com.iury.backendsenior.model.Album;
import com.iury.backendsenior.model.Artista;
import com.iury.backendsenior.repository.AlbumRepository;
import com.iury.backendsenior.repository.ArtistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistaRepository artistaRepository;

    @Transactional
    public Album salvar(Album album, List<Long> artistaIds) {
        // Regra de Negócio: Validar se os artistas informados existem
        if (artistaIds != null && !artistaIds.isEmpty()) {
            List<Artista> artistas = artistaRepository.findAllById(artistaIds);
            album.setArtistas(artistas);
        }
        return albumRepository.save(album);
    }

    // Requisito: "Paginação na consulta dos álbuns"
    public Page<Album> listar(Pageable pageable) {
        return albumRepository.findAll(pageable);
    }

    public Optional<Album> buscarPorId(Long id) {
        return albumRepository.findById(id);
    }
    
    // O Upload de imagem (MinIO) entrará aqui depois
}