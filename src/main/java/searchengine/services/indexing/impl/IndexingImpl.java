package searchengine.services.indexing.impl;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.exceptions.NotInConfigurationException;
import searchengine.services.indexing.IndexingService;
import searchengine.utils.indexing.executor.Executor;
import searchengine.web.model.IndexingResponse;
import searchengine.web.model.UpsertIndexingPageRequest;

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
  public IndexingResponse startIndexing() {
    if (isIndexingAlreadyRunning()) {
      return new IndexingResponse(false, "Индексация уже запущена");
    }

    CompletableFuture.runAsync(executor::executeSeveralPagesIndexing);
    return new IndexingResponse(true, "");
  }

  private Boolean isIndexingAlreadyRunning() {
    return !isIndexing;
  }

  @Override
  public IndexingResponse stopIndexing() {
    if (!isIndexing) return new IndexingResponse(false, "Индексация не запущена");
    setIsIndexingToFalse();
    return new IndexingResponse(true, "Индексация остановлена пользователем");
  }

  private void setIsIndexingToFalse() {
    isIndexing = false;
  }

  @SneakyThrows
  @Override
  public IndexingResponse indexPage(UpsertIndexingPageRequest upsertIndexingPageRequest) {
    return CompletableFuture.supplyAsync(
            () -> {
              try {
                executor.executeOnePageIndexing(upsertIndexingPageRequest.getUrl());
                return new IndexingResponse(true, "");
              } catch (NotInConfigurationException | URISyntaxException e) {
                return new IndexingResponse(false, e.getLocalizedMessage());
              }
            })
        .get();
  }
}
