package com.batch;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableCaching
public class CachingConfig {

    @Bean
    @Primary
    public CacheManager cacheManagerForRecipes() {
        return new ConcurrentMapCacheManager("recipes");
    }

    @Bean
    @Qualifier(value = "Batch")
    public CacheManager cacheManagerForBatches() {
        return new ConcurrentMapCacheManager("batches");
    }

}
