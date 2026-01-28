package com.iury.backendsenior.dto.autenticacao;

import com.iury.backendsenior.model.UserRole;

public record RegisterDTO(String login, String senha, UserRole role) {
}