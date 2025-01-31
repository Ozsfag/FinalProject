package searchengine.configuration;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LockConfiguration {

  @Bean
  ReentrantReadWriteLock lock() {
    return new ReentrantReadWriteLock();
  }
}
