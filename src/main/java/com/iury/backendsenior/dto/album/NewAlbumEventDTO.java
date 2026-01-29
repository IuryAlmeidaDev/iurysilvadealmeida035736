package com.iury.backendsenior.dto.album;

public record NewAlbumEventDTO(
    Long id, 
    String titulo, 
    Integer anoLancamento
) {}
