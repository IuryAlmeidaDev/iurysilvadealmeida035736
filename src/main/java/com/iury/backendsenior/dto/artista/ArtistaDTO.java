package com.iury.backendsenior.dto.artista;

import com.iury.backendsenior.model.enums.TipoArtista;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ArtistaDTO(
    Long id,

    @NotBlank(message = "O nome não pode estar em branco")
    @Size(min = 3, message = "O nome deve ter no mínimo 3 caracteres")
    String nome,

    @NotNull(message = "O tipo do artista é obrigatório (CANTOR ou BANDA)")
    TipoArtista tipo
) {
}