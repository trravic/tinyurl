package com.shorturl.tokenservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;
   

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        logger.info("Creating JedisConnectionFactory with host: {} and port: {}", redisHost, redisPort);
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        JedisConnectionFactory factory = new JedisConnectionFactory(config);
        factory.afterPropertiesSet(); // ensures factory is initialized
        logger.info("JedisConnectionFactory created successfully");
        return factory;
    }
    

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        logger.info("Creating RedisTemplate...");
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet(); // ensures template is initialized
        logger.info("RedisTemplate created successfully");
        return template;
    }

    @Bean
    public RedisCacheManager cacheManager(JedisConnectionFactory jedisConnectionFactory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .entryTtl(Duration.ofMinutes(10)) // TTL for cache entries
                .disableCachingNullValues();

        return RedisCacheManager.builder(jedisConnectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }
}

