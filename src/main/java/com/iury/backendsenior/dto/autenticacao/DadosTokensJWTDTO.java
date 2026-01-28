package com.iury.backendsenior.dto.autenticacao;

public record DadosTokensJWTDTO(
        String accessToken,
        String refreshToken
) {}
