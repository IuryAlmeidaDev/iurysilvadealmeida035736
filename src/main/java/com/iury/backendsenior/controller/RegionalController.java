package com.iury.backendsenior.controller;

import com.iury.backendsenior.model.Regional;
import com.iury.backendsenior.service.RegionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/regionais")
@RequiredArgsConstructor
public class RegionalController {

    private final RegionalService regionalService;

    @GetMapping
    public ResponseEntity<List<Regional>> listar(
            @RequestParam(required = false) Boolean ativo
    ) {
        return ResponseEntity.ok(regionalService.listar(ativo));
    }

    @PostMapping("/sincronizar")
    public ResponseEntity<RegionalService.ResultadoSync> sincronizar() {
        return ResponseEntity.ok(regionalService.sincronizar());
    }
}
