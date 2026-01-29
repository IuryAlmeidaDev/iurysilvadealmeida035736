package com.iury.backendsenior.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import redis.embedded.RedisServer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public abstract class IntegrationTestBase {

    private static RedisServer redisServer;

    @PostConstruct
    public void startRedis() {
        if (redisServer == null) {
            try {
                redisServer = new RedisServer(6379);
                redisServer.start();
                System.out.println(">>> Redis em Memória iniciado com sucesso na porta 6379");
            } catch (Exception e) {
                System.err.println(">>> Aviso: Redis em memória não iniciado: " + e.getMessage());
            }
        }
    }

    @PreDestroy
    public void stopRedis() {
        try {
            if (redisServer != null) {
                redisServer.stop();
                System.out.println(">>> Redis em Memória finalizado");
            }
        } catch (Exception e) {
            System.err.println(">>> Erro ao parar Redis: " + e.getMessage());
        }
    }
}