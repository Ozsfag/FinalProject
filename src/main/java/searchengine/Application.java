package searchengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
@EnableJpaRepositories(bootstrapMode = BootstrapMode.LAZY)
@EnableAspectJAutoProxy
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
