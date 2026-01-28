package com.iury.backendsenior.dto.album;

import java.util.List;

import com.iury.backendsenior.dto.artista.ArtistaDTO;

public record AlbumResponseDTO(
    Long id,
    String titulo,
    Integer anoLancamento,
    String capaUrl,
    List<String> imagens,
    List<ArtistaDTO> artistas
) {}
