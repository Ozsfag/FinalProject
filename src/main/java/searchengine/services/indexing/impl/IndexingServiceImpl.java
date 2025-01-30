package searchengine.services.indexing.impl;

import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.exceptions.IndexingAlreadyRunningException;
import searchengine.exceptions.StoppedExecutionException;
import searchengine.services.indexing.IndexingService;
import searchengine.utils.indexing.executor.Executor;
import searchengine.web.models.IndexingResponse;
import searchengine.web.models.StoppingResponse;
import searchengine.web.models.UpsertIndexingPageRequest;

@Service
@Slf4j
public class IndexingServiceImpl implements IndexingService {

  public static volatile Boolean isIndexing = true;
  private final Executor executor;

  public IndexingServiceImpl(Executor executor) {
    this.executor = executor;
  }

  @Override
  public IndexingResponse startIndexing() {
    if (isIndexingAlreadyRunning()) {
      throw new IndexingAlreadyRunningException("Индексация уже запущена");
    }

    CompletableFuture.runAsync(executor::executeSeveralPagesIndexing);
    return new IndexingResponse(true, "Indexing complete successfully");
  }

  private Boolean isIndexingAlreadyRunning() {
    return !isIndexing;
  }

  @Override
  public StoppingResponse stopIndexing() {
    if (isIndexing) {
      isIndexing = false;
      return new StoppingResponse(true, "Индексация остановлена пользователем");
    }
    throw new StoppedExecutionException("Индексация еще не запущена");
  }

  @Override
  public IndexingResponse indexPage(UpsertIndexingPageRequest upsertIndexingPageRequest) {
    return CompletableFuture.supplyAsync(() -> indexPageTask(upsertIndexingPageRequest)).join();
  }

  private IndexingResponse indexPageTask(UpsertIndexingPageRequest request) {
    executor.executeOnePageIndexing(request.getUrl());
    return new IndexingResponse(true, "Indexing completed successfully");
  }
}
