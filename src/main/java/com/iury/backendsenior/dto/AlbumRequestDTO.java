package com.iury.backendsenior.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AlbumRequestDTO(
    @NotBlank(message = "O título não pode estar em branco")
    @Size(min = 2, message = "O título deve ter no mínimo 2 caracteres")
    String titulo,

    @NotNull(message = "O ano de lançamento é obrigatório")
    Integer anoLancamento,

    @NotNull(message = "A lista de artistas é obrigatória")
    List<Long> artistaIds
) {}
