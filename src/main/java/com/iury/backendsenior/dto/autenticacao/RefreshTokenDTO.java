package com.iury.backendsenior.dto.autenticacao;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenDTO(
        @NotBlank String refreshToken
) {}
