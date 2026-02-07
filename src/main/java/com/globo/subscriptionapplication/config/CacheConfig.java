package com.globo.subscriptionapplication.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // TTL padrão: 10 minutos
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJacksonJsonRedisSerializer(new ObjectMapper())));
        // Configurações específicas por cache
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Cache de usuários - TTL mais longo pois muda menos
        cacheConfigurations.put("users", defaultConfig.entryTtl(Duration.ofHours(1)));

        // Cache de assinaturas - TTL médio
        cacheConfigurations.put("subscriptions", defaultConfig.entryTtl(Duration.ofMinutes(30)));

        // Cache de assinaturas ativas - TTL curto pois pode mudar rapidamente
        cacheConfigurations.put("activeSubscriptions", defaultConfig.entryTtl(Duration.ofMinutes(5)));

        // Cache de histórico de pagamentos - TTL longo pois é histórico
        cacheConfigurations.put("paymentHistory", defaultConfig.entryTtl(Duration.ofHours(2)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
