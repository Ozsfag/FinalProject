package searchengine.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@Component
public class FjpComponent {
    private static  volatile ForkJoinPool forkJoinPool;

    private FjpComponent() {
    }
    public static ForkJoinPool getInstance(){
        if (forkJoinPool == null){
            forkJoinPool = new ForkJoinPool();
        }
        return forkJoinPool;
    }
}
