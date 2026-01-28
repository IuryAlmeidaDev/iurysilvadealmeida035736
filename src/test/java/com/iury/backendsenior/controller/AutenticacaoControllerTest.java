package com.iury.backendsenior.controller;

import com.iury.backendsenior.dto.autenticacao.DadosAutenticacaoDTO;
import com.iury.backendsenior.dto.autenticacao.RefreshTokenDTO;
import com.iury.backendsenior.dto.autenticacao.RegisterDTO;
import com.iury.backendsenior.model.UserRole;
import com.iury.backendsenior.model.Usuario;
import com.iury.backendsenior.repository.UsuarioRepository;
import com.iury.backendsenior.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AutenticacaoController.class)
@AutoConfigureMockMvc(addFilters = false)
class AutenticacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private TokenService tokenService;

    private final String URL_BASE = "/v1/auth";

    @Test
    @DisplayName("POST /v1/auth/register - deve retornar 400 quando login já existe")
    void register_deveRetornar400_quandoLoginJaExiste() throws Exception {
        var dto = new RegisterDTO("iury", "123456", UserRole.USER);
        Usuario usuarioExistente = new Usuario("iury", "senha", UserRole.USER);

        when(usuarioRepository.findByLogin("iury")).thenReturn(usuarioExistente);

        mockMvc.perform(post(URL_BASE + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("POST /v1/auth/register - deve cadastrar usuário com sucesso")
    void register_deveCadastrarUsuario() throws Exception {
        var dto = new RegisterDTO("iury", "123456", UserRole.USER);

        when(usuarioRepository.findByLogin("iury")).thenReturn(null);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        when(usuarioRepository.save(captor.capture())).thenReturn(null);

        mockMvc.perform(post(URL_BASE + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        var usuarioSalvo = captor.getValue();
        assertThat(usuarioSalvo.getLogin()).isEqualTo("iury");
        assertThat(usuarioSalvo.getSenha()).startsWith("$2");
    }

    @Test
    @DisplayName("POST /v1/auth/login - deve retornar accessToken e refreshToken")
    void login_deveRetornarTokens() throws Exception {
        var dto = new DadosAutenticacaoDTO("iury", "123456");
        Usuario usuario = new Usuario("iury", "hash", UserRole.USER);
        Authentication authMock = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(usuario);

        when(tokenService.gerarToken(usuario)).thenReturn("access-token");
        when(tokenService.gerarRefreshToken(usuario)).thenReturn("refresh-token");

        mockMvc.perform(post(URL_BASE + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    @DisplayName("POST /v1/auth/refresh - deve gerar novo accessToken")
    void refresh_deveGerarNovoToken() throws Exception {
        var dto = new RefreshTokenDTO("refresh-valido");
        Usuario usuario = new Usuario("iury", "hash", UserRole.USER);

        when(tokenService.validarRefreshToken("refresh-valido")).thenReturn("iury");
        when(usuarioRepository.findByLogin("iury")).thenReturn(usuario);
        when(tokenService.gerarToken(usuario)).thenReturn("novo-access-token");

        mockMvc.perform(post(URL_BASE + "/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("novo-access-token"));
    }

    @Test
    @DisplayName("POST /v1/auth/refresh - deve retornar 401 quando token é inválido")
    void refresh_deveRetornar401_quandoInvalido() throws Exception {
        var dto = new RefreshTokenDTO("refresh-invalido");

        when(tokenService.validarRefreshToken(anyString())).thenReturn("");

        mockMvc.perform(post(URL_BASE + "/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());

        verify(usuarioRepository, never()).findByLogin(anyString());
    }
}