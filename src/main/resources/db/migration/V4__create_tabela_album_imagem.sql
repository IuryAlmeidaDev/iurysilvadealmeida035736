CREATE TABLE album_imagem (
    id BIGSERIAL PRIMARY KEY,
    album_id BIGINT NOT NULL,
    nome_arquivo VARCHAR(255) NOT NULL,
    ordem INTEGER,

    CONSTRAINT fk_album_imagem_album
        FOREIGN KEY (album_id)
        REFERENCES album(id)
        ON DELETE CASCADE
);

-- Índice para otimizar busca de imagens por álbum
CREATE INDEX idx_album_imagem_album_id
    ON album_imagem(album_id);
