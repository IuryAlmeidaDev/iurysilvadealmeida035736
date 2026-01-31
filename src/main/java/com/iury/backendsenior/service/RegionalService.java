package com.iury.backendsenior.service;

import com.iury.backendsenior.dto.regional.RegionalIntegradorDTO;
import com.iury.backendsenior.model.Regional;
import com.iury.backendsenior.repository.RegionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegionalService {

    private final RegionalRepository regionalRepository;
    private final RegionalIntegradorClient integradorClient;

    public record ResultadoSync(int inseridos, int inativados, int atualizadosPorMudanca) {}

    public List<Regional> listar(Boolean ativo) {
        if (ativo == null) return regionalRepository.findAll();
        return regionalRepository.findByAtivo(ativo);
    }

    @Transactional
    public ResultadoSync sincronizar() {
        List<RegionalIntegradorDTO> remotos = integradorClient.buscarRegionais();

        Set<Integer> idsRemotos = remotos.stream()
                .map(RegionalIntegradorDTO::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Integer, String> remotoPorId = remotos.stream()
                .filter(r -> r.id() != null && r.nome() != null)
                .collect(Collectors.toMap(
                        RegionalIntegradorDTO::id,
                        RegionalIntegradorDTO::nome,
                        (a, b) -> b
                ));

        List<Regional> ativosLocais = regionalRepository.findByAtivoTrue();
        Map<Integer, Regional> localAtivoPorId = ativosLocais.stream()
                .filter(r -> r.getId() != null)
                .collect(Collectors.toMap(
                        Regional::getId,
                        r -> r,
                        (a, b) -> a
                ));

        int inseridos = 0;
        int inativados = 0;
        int atualizados = 0;

        for (var entry : remotoPorId.entrySet()) {
            Integer id = entry.getKey();
            String nome = entry.getValue();

            Regional localAtivo = localAtivoPorId.get(id);

            if (localAtivo == null) {
                regionalRepository.save(Regional.builder()
                        .id(id)
                        .nome(nome)
                        .ativo(true)
                        .build());
                inseridos++;
                continue;
            }

            if (!Objects.equals(localAtivo.getNome(), nome)) {
                localAtivo.setAtivo(false);
                regionalRepository.save(localAtivo);
                inativados++;

                regionalRepository.save(Regional.builder()
                        .id(id)
                        .nome(nome)
                        .ativo(true)
                        .build());
                atualizados++;
            }
        }

        for (Regional local : ativosLocais) {
            if (local.getId() != null && !idsRemotos.contains(local.getId())) {
                local.setAtivo(false);
                regionalRepository.save(local);
                inativados++;
            }
        }

        return new ResultadoSync(inseridos, inativados, atualizados);
    }
}
