package com.iury.backendsenior.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    @Value("${app.rate-limit.requests-per-minute:10}")
    private int rpm;

    @Value("${app.rate-limit.enabled:true}")
    private boolean enabled;

    @Value("${app.rate-limit.key-prefix:rl_}")
    private String keyPrefix;

    @Bean(destroyMethod = "close")
    public StatefulRedisConnection<String, byte[]> bucket4jRedisConnection(
            @Value("${spring.data.redis.host:localhost}") String host,
            @Value("${spring.data.redis.port:6379}") int port
    ) {
        RedisClient client = RedisClient.create(RedisURI.Builder.redis(host, port).build());
        
        // Combina String para chaves e byte[] para os valores do Bucket4j
        return client.connect(RedisCodec.of(StringCodec.UTF8, new ByteArrayCodec()));
    }

    @Bean
    public ProxyManager<String> proxyManager(StatefulRedisConnection<String, byte[]> connection) {
        return LettuceBasedProxyManager.builderFor(connection).build();
    }

    @Bean
    public BucketConfiguration bucketConfiguration() {
        // Substituído o .simple() pelo builder moderno para evitar @Deprecated
        Bandwidth limit = Bandwidth.builder()
                .capacity(rpm)
                .refillGreedy(rpm, Duration.ofMinutes(1))
                .build();

        return BucketConfiguration.builder()
                .addLimit(limit)
                .build();
    }

    @Bean
    public DistributedRateLimitFilter rateLimitFilter(ProxyManager<String> proxyManager, BucketConfiguration config) {
        return new DistributedRateLimitFilter(
                proxyManager, 
                () -> config, 
                keyPrefix, 
                rpm, 
                enabled
        );
    }
}