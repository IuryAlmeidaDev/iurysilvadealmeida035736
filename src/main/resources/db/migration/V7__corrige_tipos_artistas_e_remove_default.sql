-- 1) Corrige os artistas do seed que foram marcados como CANTOR por DEFAULT no V5
UPDATE artista
SET tipo = 'BANDA'
WHERE nome IN ('Guns N'' Roses');

UPDATE artista
SET tipo = 'CANTOR'
WHERE nome IN ('Serj Tankian', 'Mike Shinoda', 'Michel Teló')
  AND (tipo IS NULL OR tipo <> 'CANTOR');

ALTER TABLE artista
ALTER COLUMN tipo DROP DEFAULT;
