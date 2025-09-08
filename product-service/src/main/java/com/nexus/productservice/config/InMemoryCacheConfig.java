package com.nexus.productservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;

@Configuration
@EnableCaching
@Profile("dev")
@Slf4j
public class InMemoryCacheConfig {

    @PostConstruct
    public void init() {
        log.info("🚀 CACHE CONFIGURATION: Using IN-MEMORY CACHE for Development Environment");
    }

    @Bean
    public CacheManager cacheManager() {
        log.info("🚀 Initializing In-Memory Cache Manager (ConcurrentMapCacheManager)");
        CacheManager cacheManager = new ConcurrentMapCacheManager("products");
        log.info("🚀 In-Memory Cache Manager successfully configured and ready to use");
        return cacheManager;
    }
}
