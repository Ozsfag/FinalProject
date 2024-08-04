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

    /**
     * Creates a custom cache manager using Caffeine as the underlying cache implementation.
     * The cache manager is configured to expire entries after 7 minutes since their last write.
     * The cache manager uses the provided ForkJoinPool for executing cache operations.
     *
     * @return the cache manager instance
     */
    @Bean("customCacheManager")
    public CacheManager cacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .executor(forkJoinPool));
        return caffeineCacheManager;
    }
}