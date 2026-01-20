package com.iury.backendsenior.dto;

import java.util.List;

public record AlbumResponseDTO(
    Long id,
    String titulo,
    Integer anoLancamento,
    String capaUrl,
    List<ArtistaDTO> artistas
) {}