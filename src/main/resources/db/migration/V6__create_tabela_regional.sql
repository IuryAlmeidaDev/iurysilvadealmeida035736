CREATE TABLE regional (
    pk BIGSERIAL PRIMARY KEY,
    id INTEGER NOT NULL,
    nome VARCHAR(200) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_regional_id_ativo ON regional(id, ativo);
