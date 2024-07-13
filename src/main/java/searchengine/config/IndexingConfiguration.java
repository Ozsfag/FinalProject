package searchengine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import searchengine.dto.indexing.responseImpl.ConnectionResponse;

import java.util.concurrent.ForkJoinPool;

@Configuration
public class IndexingConfiguration {

    /**
     * Returns the common ForkJoinPool instance.
     *
     * @return the common ForkJoinPool instance
     */
    @Bean
    public ForkJoinPool forkJoinPool(){
        return ForkJoinPool.commonPool();
    }

    /**
     * Returns a new instance of the ConnectionResponse class.
     *
     * @return a new instance of ConnectionResponse
     */
    @Bean
    public ConnectionResponse connectionResponse(){
        return new ConnectionResponse();
    }
}
