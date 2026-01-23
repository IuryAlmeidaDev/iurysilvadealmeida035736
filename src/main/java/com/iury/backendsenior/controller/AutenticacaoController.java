package com.iury.backendsenior.controller;

import com.iury.backendsenior.dto.DadosAutenticacao;
import com.iury.backendsenior.dto.DadosTokenJWT;
import com.iury.backendsenior.dto.DadosTokensJWT;
import com.iury.backendsenior.dto.RefreshTokenDTO;
import com.iury.backendsenior.dto.RegisterDTO;
import com.iury.backendsenior.model.Usuario;
import com.iury.backendsenior.repository.UsuarioRepository;
import com.iury.backendsenior.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AuthenticationManager manager;
    private final TokenService tokenService;
    private final UsuarioRepository repository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO data) {
        if (this.repository.findByLogin(data.login()) != null) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.senha());
        Usuario newUser = new Usuario(data.login(), encryptedPassword, data.role());

        this.repository.save(newUser);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<DadosTokensJWT> efetuarLogin(@RequestBody @Valid DadosAutenticacao dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());

        var authentication = manager.authenticate(authenticationToken);

        var usuario = (Usuario) authentication.getPrincipal();

        var accessToken = tokenService.gerarToken(usuario);
        var refreshToken = tokenService.gerarRefreshToken(usuario);

        return ResponseEntity.ok(new DadosTokensJWT(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<DadosTokenJWT> refresh(@RequestBody @Valid RefreshTokenDTO dto) {
        String login = tokenService.validarRefreshToken(dto.refreshToken());

        if (login.isBlank()) {
            return ResponseEntity.status(401).build();
        }

        Usuario usuario = repository.findByLogin(login);
        if (usuario == null) {
            return ResponseEntity.status(401).build();
        }

        String novoAccessToken = tokenService.gerarToken(usuario);
        return ResponseEntity.ok(new DadosTokenJWT(novoAccessToken));
    }
}