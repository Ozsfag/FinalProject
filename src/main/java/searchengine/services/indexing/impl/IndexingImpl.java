package searchengine.services.indexing.impl;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.concurrent.CompletableFuture;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.dto.ResponseInterface;
import searchengine.dto.indexing.responseImpl.Bad;
import searchengine.dto.indexing.responseImpl.Stop;
import searchengine.dto.indexing.responseImpl.Successful;
import searchengine.exceptions.NotInConfigurationException;
import searchengine.services.indexing.IndexingService;
import searchengine.utils.indexing.executor.Executor;

@Service
@Slf4j
public class IndexingImpl implements IndexingService {

  public static volatile Boolean isIndexing = true;
  private final Executor executor;

  public IndexingImpl(Executor executor) {
    this.executor = executor;
  }

  @SuppressFBWarnings("PA_PUBLIC_PRIMITIVE_ATTRIBUTE")
  @Override
  public ResponseInterface startIndexing() {
    if (isIndexingAlreadyRunning()) {
      return new Bad(false, "Индексация уже запущена");
    }

    CompletableFuture.runAsync(executor::executeSeveralPagesIndexing);
    return new Successful(true);
  }

  private Boolean isIndexingAlreadyRunning() {
    return !isIndexing;
  }

  @Override
  public ResponseInterface stopIndexing() {
    if (!isIndexing) return new Stop(false, "Индексация не запущена");
    setIsIndexingToFalse();
    return new Stop(true, "Индексация остановлена пользователем");
  }

  private void setIsIndexingToFalse() {
    isIndexing = false;
  }

  @SneakyThrows
  @Override
  public ResponseInterface indexPage(String url) {
    return CompletableFuture.supplyAsync(
            () -> {
              try {
                executor.executeOnePageIndexing(url);
                return new Successful(true);
              } catch (NotInConfigurationException e) {
                return new Bad(false, e.getLocalizedMessage());
              }
            })
        .get();
  }
}
