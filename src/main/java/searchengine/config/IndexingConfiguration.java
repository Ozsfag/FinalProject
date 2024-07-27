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
    public ForkJoinPool forkJoinPool(){return ForkJoinPool.commonPool();
    }
//    java.util.concurrent.ForkJoinPool@372f7bc[Running, parallelism = 3, size = 2, active = 2, running = 2, steals = 0, tasks = 0, submissions = 0]

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
