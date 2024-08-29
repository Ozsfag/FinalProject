package searchengine.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Collection;
import java.util.concurrent.ForkJoinPool;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CachingConfig implements CacheManager {

  @Lazy private final ForkJoinPool forkJoinPool;
  private CaffeineCacheManager caffeineCacheManager;

  @Primary
  @Bean("customCacheManager")
  public CacheManager cacheManager() {
    caffeineCacheManager = new CaffeineCacheManager();
    caffeineCacheManager.setCaffeine(
        Caffeine.newBuilder().executor(forkJoinPool));
    return caffeineCacheManager;
  }

  @Override
  public Cache getCache(@NonNull String name) {
//    Cache value = caffeineCacheManager.getCache(name);
      return null;
  }

  @Override
  public Collection<String> getCacheNames() {
    return caffeineCacheManager.getCacheNames();
  }
}
