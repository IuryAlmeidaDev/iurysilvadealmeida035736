-- 1. Inserção dos Artistas
-- Nota: Usei duas aspas simples ('') em Guns N' Roses para o banco entender que é parte do nome
INSERT INTO artista (nome) VALUES
('Serj Tankian'),
('Mike Shinoda'),
('Michel Teló'),
('Guns N'' Roses');

-- 2. Inserção dos Álbuns
INSERT INTO album (titulo) VALUES
('Harakiri'),
('Black Blooms'),
('The Rough Dog'),
('The Rising Tied'),
('Post Traumatic'),
('Post Traumatic EP'),
('Where''d You Go'), -- Aspa escapada
('Bem Sertanejo'),
('Bem Sertanejo - O Show (Ao Vivo)'),
('Bem Sertanejo - (1ª Temporada) - EP'),
('Use Your Illusion I'),
('Use Your Illusion II'),
('Greatest Hits');

-- 3. Relacionamentos (Artista <-> Album)
INSERT INTO artista_album (artista_id, album_id)
SELECT a.id, al.id
FROM artista a, album al
WHERE a.nome = 'Serj Tankian'
  AND al.titulo IN ('Harakiri', 'Black Blooms', 'The Rough Dog');

INSERT INTO artista_album (artista_id, album_id)
SELECT a.id, al.id
FROM artista a, album al
WHERE a.nome = 'Mike Shinoda'
  AND al.titulo IN (
    'The Rising Tied',
    'Post Traumatic',
    'Post Traumatic EP',
    'Where''d You Go' -- Importante: O nome aqui deve ser identico ao inserido na tabela album
  );

-- Michel Teló
INSERT INTO artista_album (artista_id, album_id)
SELECT a.id, al.id
FROM artista a, album al
WHERE a.nome = 'Michel Teló'
  AND al.titulo IN (
    'Bem Sertanejo',
    'Bem Sertanejo - O Show (Ao Vivo)',
    'Bem Sertanejo - (1ª Temporada) - EP'
  );

-- Guns N' Roses
INSERT INTO artista_album (artista_id, album_id)
SELECT a.id, al.id
FROM artista a, album al
WHERE a.nome = 'Guns N'' Roses' -- Busca com a aspa escapada
  AND al.titulo IN (
    'Use Your Illusion I',
    'Use Your Illusion II',
    'Greatest Hits'
  );