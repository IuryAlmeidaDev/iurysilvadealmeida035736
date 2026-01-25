package com.iury.backendsenior.model;

import com.iury.backendsenior.model.enums.TipoArtista;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "artista")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Artista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TipoArtista tipo = TipoArtista.CANTOR;

    @ManyToMany(mappedBy = "artistas")
    private List<Album> albuns;
}
