package com.iury.backendsenior.dto;

import com.iury.backendsenior.model.enums.TipoArtista;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArtistaResponseDTO {

    private Long id;
    private String nome;
    private TipoArtista tipo;
}
