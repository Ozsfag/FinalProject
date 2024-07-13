package searchengine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import searchengine.dto.indexing.responseImpl.ConnectionResponse;

import java.util.concurrent.ForkJoinPool;

@Configuration
public class IndexingConfiguration {
    @Bean
    public ForkJoinPool forkJoinPool(){
        return ForkJoinPool.commonPool();
    }
    @Bean
    public ConnectionResponse connectionResponse(){
        return new ConnectionResponse();
    }
}
