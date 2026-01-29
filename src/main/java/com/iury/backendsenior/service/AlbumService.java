package com.iury.backendsenior.service;

import com.iury.backendsenior.dto.album.NewAlbumEventDTO;
import com.iury.backendsenior.model.Album;
import com.iury.backendsenior.model.AlbumImagem;
import com.iury.backendsenior.model.Artista;
import com.iury.backendsenior.model.enums.TipoArtista;
import com.iury.backendsenior.repository.AlbumImagemRepository;
import com.iury.backendsenior.repository.AlbumRepository;
import com.iury.backendsenior.repository.ArtistaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumImagemRepository albumImagemRepository;
    private final ArtistaRepository artistaRepository;
    private final MinioService minioService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Album salvar(Album album, List<Long> artistaIds) {
        List<Artista> artistas = artistaRepository.findAllById(artistaIds);
        album.setArtistas(artistas);

        Album albumSalvo = albumRepository.save(album);

        NewAlbumEventDTO event = new NewAlbumEventDTO(
                albumSalvo.getId(),
                albumSalvo.getTitulo(),
                albumSalvo.getAnoLancamento()
        );

        messagingTemplate.convertAndSend("/topic/new-album", event);

        return albumSalvo;
    }

    public Page<Album> listar(Pageable pageable, TipoArtista tipoArtista) {
        if (tipoArtista != null) {
            return albumRepository.findDistinctByArtistas_Tipo(tipoArtista, pageable);
        }
        return albumRepository.findAll(pageable);
    }

    @Transactional
    public Album atualizar(Long id, Album albumAtualizado, List<Long> novosArtistasIds) {
        Album albumExistente = buscarPorId(id);

        albumExistente.setTitulo(albumAtualizado.getTitulo());
        albumExistente.setAnoLancamento(albumAtualizado.getAnoLancamento());

        if (novosArtistasIds != null) {
            List<Artista> novosArtistas = artistaRepository.findAllById(novosArtistasIds);
            albumExistente.setArtistas(novosArtistas);
        }

        return albumRepository.save(albumExistente);
    }

    @Transactional
    public Album atualizarCapa(Long id, MultipartFile arquivo) {
        Album album = buscarPorId(id);

        String nomeArquivo = minioService.uploadArquivo(arquivo);
        album.setCapaUrl(nomeArquivo);

        return albumRepository.save(album);
    }

    public Album buscarPorId(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Álbum não encontrado com id: " + id));
    }

    @Transactional
    public void deletar(Long id) {
        if (!albumRepository.existsById(id)) {
            throw new EntityNotFoundException("Álbum não encontrado com id: " + id);
        }
        albumRepository.deleteById(id);
    }

    @Transactional
    public List<String> adicionarImagens(Long albumId, MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("Nenhum arquivo enviado");
        }

        Album album = buscarPorId(albumId);

        int ordem = albumImagemRepository.findByAlbumIdOrderByOrdemAsc(albumId).stream()
                .map(AlbumImagem::getOrdem)
                .filter(o -> o != null)
                .max(Integer::compareTo)
                .orElse(0) + 1;

        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {
            String nomeArquivo = minioService.uploadArquivo(file);

            AlbumImagem imagem = new AlbumImagem();
            imagem.setAlbum(album);
            imagem.setNomeArquivo(nomeArquivo);
            imagem.setOrdem(ordem++);

            albumImagemRepository.save(imagem);

            urls.add(minioService.gerarUrlPreAssinada(nomeArquivo));
        }

        return urls;
    }
}
