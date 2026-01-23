package com.iury.backendsenior.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "album_imagem")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AlbumImagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    @Column(name = "nome_arquivo", nullable = false)
    private String nomeArquivo;

    private Integer ordem;
}
