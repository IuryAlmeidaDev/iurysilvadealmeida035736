package com.iury.backendsenior.dto.artista;

import com.iury.backendsenior.model.enums.TipoArtista;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ArtistaRequestDTO {

    @NotBlank
    private String nome;

    @NotNull
    private TipoArtista tipo;
}
