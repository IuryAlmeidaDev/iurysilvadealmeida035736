-- Criação da tabela de Artistas
-- Decisão: Usei SERIAL para o ID para facilitar a autoincrementação no Postgres
CREATE TABLE artista (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Criação da tabela de Álbuns
CREATE TABLE album (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    ano_lancamento INTEGER,
    capa_url VARCHAR(500), -- Armazena a referência da imagem no MinIO
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela associativa para o relacionamento N:N (Muitos-para-Muitos)
-- Decisão: Um artista pode ter vários álbuns e um álbum pode ter vários artistas (ex: Feats)
CREATE TABLE artista_album (
    artista_id INTEGER NOT NULL,
    album_id INTEGER NOT NULL,
    CONSTRAINT fk_artista FOREIGN KEY (artista_id) REFERENCES artista (id) ON DELETE CASCADE,
    CONSTRAINT fk_album FOREIGN KEY (album_id) REFERENCES album (id) ON DELETE CASCADE,
    PRIMARY KEY (artista_id, album_id)
);

-- Índices para otimizar as consultas (Performance)
CREATE INDEX idx_artista_nome ON artista(nome);
CREATE INDEX idx_album_titulo ON album(titulo);