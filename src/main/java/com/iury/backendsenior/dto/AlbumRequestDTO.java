package com.iury.backendsenior.dto;

import java.util.List;

public record AlbumRequestDTO(
    String titulo,
    Integer anoLancamento,
    List<Long> artistaIds 