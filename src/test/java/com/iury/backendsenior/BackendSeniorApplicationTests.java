package com.iury.backendsenior;

import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Teste de contexto desabilitado para evitar dependência de variáveis de ambiente (JWT/MINIO/DB) nos testes unitários")
@SpringBootTest
class BackendSeniorApplicationTests {
}
