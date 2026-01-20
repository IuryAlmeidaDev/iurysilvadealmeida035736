package com.iury.backendsenior.service;

import com.iury.backendsenior.model.Album;
import com.iury.backendsenior.model.Artista;
import com.iury.backendsenior.repository.AlbumRepository;
import com.iury.backendsenior.repository.ArtistaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository repository;
    private final ArtistaRepository artistaRepository;
    private final MinioService minioService; 

    @Transactional
    public Album salvar(Album album, List<Long> artistaIds) {
        List<Artista> artistas = artistaRepository.findAllById(artistaIds);
        album.setArtistas(artistas);
        return repository.save(album);
    }

    public Page<Album> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional
    public Album atualizar(Long id, Album albumAtualizado, List<Long> novosArtistasIds) {
        Album albumExistente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Álbum não encontrado com id: " + id));

        albumExistente.setTitulo(albumAtualizado.getTitulo());
        albumExistente.setAnoLancamento(albumAtualizado.getAnoLancamento());

        if (novosArtistasIds != null) {
            List<Artista> novosArtistas = artistaRepository.findAllById(novosArtistasIds);
            albumExistente.setArtistas(novosArtistas);
        }

        return repository.save(albumExistente);
    }

    @Transactional
    public Album atualizarCapa(Long id, MultipartFile arquivo) {
        Album album = buscarPorId(id);

        String nomeArquivo = minioService.uploadArquivo(arquivo);

        album.setCapaUrl(nomeArquivo); 
        
        return repository.save(album);
    }

    public Album buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Álbum não encontrado com id: " + id));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Álbum não encontrado com id: " + id);
        }
        repository.deleteById(id);
    }
}