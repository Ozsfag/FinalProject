package searchengine.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CachingConfig {
    @Lazy
    private final ForkJoinPool forkJoinPool;
    @Bean("customCacheManager")
    public CacheManager cacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(7, TimeUnit.MINUTES)
                .executor(forkJoinPool));
        return caffeineCacheManager;
    }
    @Bean
    public CustomKeyGenerator customKeyGenerator(){return new CustomKeyGenerator();}
}