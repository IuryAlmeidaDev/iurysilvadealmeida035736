package com.iury.backendsenior.dto;

import java.util.List;

public record ErroValidacaoDTO(
    int status,
    String mensagem,
    List<CampoErro> erros
) {
    public record CampoErro(String campo, String erro) {}
}