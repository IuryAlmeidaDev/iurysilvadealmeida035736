CREATE TABLE artista (
    id BIGSERIAL PRIMARY KEY, 
    nome VARCHAR(255) NOT NULL
);

CREATE TABLE album (
    id BIGSERIAL PRIMARY KEY, 
    titulo VARCHAR(255) NOT NULL,
    ano_lancamento INTEGER,
    capa_url VARCHAR(255)
);

CREATE TABLE artista_album (
    artista_id BIGINT NOT NULL, 
    album_id BIGINT NOT NULL,   
    PRIMARY KEY (artista_id, album_id),
    CONSTRAINT fk_artista FOREIGN KEY (artista_id) REFERENCES artista(id),
    CONSTRAINT fk_album FOREIGN KEY (album_id) REFERENCES album(id)
);

-- Índices para otimizar as consultas (Performance)
CREATE INDEX idx_artista_nome ON artista(nome);
CREATE INDEX idx_album_titulo ON album(titulo);