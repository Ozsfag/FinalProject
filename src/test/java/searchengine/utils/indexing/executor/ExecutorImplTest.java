package searchengine.utils.indexing.executor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import searchengine.config.SitesList;
import searchengine.model.SiteModel;
import searchengine.utils.entityHandlers.SiteHandler;
import searchengine.utils.entitySaver.EntitySaverTemplate;
import searchengine.utils.indexing.executor.impl.ExecutorImpl;
import searchengine.utils.indexing.processor.Processor;

public class ExecutorImplTest {

  @Mock private EntitySaverTemplate<SiteModel> entitySaverTemplate;

  @Mock private SiteHandler siteHandler;

  @Mock private Processor processor;

  @Mock private SitesList sitesList;

  @Mock private ForkJoinPool forkJoinPool;

  @InjectMocks private ExecutorImpl executor;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  class ExecuteIndexingTest {

    //    @Test
    //    public void testExecuteIndexingForAllSiteModels() throws ExecutionException,
    // InterruptedException {
    //      // Arrange
    //      List<SiteModel> siteModels = List.of(new SiteModel(), new SiteModel());
    //      when(siteHandler.getIndexedSiteModelFromSites(any())).thenReturn(siteModels);
    //      when(entitySaverTemplate.saveEntities(siteModels)).thenReturn(siteModels);
    //      when(forkJoinPool.submit(any(Runnable.class))).thenAnswer(invocation -> {
    //        Runnable task = invocation.getArgument(0);
    //        CompletableFuture<Void> future = CompletableFuture.runAsync(task);
    //        future.join();
    //        return future;
    //      });
    //
    //      // Act
    //      executor.executeIndexing();
    //
    //      // Assert
    //      verify(siteHandler, times(1)).getIndexedSiteModelFromSites(any());
    //      verify(entitySaverTemplate, times(1)).saveEntities(siteModels);
    //      verify(processor, times(2)).processSiteIndexing(any(SiteModel.class));
    //
    //      // Capture the SiteModel arguments passed to the processor
    //      ArgumentCaptor<SiteModel> siteModelCaptor = ArgumentCaptor.forClass(SiteModel.class);
    //      verify(processor, times(2)).processSiteIndexing(siteModelCaptor.capture());
    //
    //      // Verify that the correct SiteModels were processed
    //      List<SiteModel> capturedSiteModels = siteModelCaptor.getAllValues();
    //      assertEquals(2, capturedSiteModels.size());
    //      assertTrue(capturedSiteModels.containsAll(siteModels));
    //
    //      // Ensure all CompletableFuture tasks are completed
    //      CompletableFuture<Void> allOf = CompletableFuture.allOf(
    //              capturedSiteModels.stream()
    //                      .map(siteModel -> CompletableFuture.runAsync(() ->
    // processor.processSiteIndexing(siteModel), forkJoinPool))
    //                      .toArray(CompletableFuture[]::new)
    //      );
    //      allOf.join();
    //      assertTrue(allOf.isDone());
    //    }

    @Test
    public void testExecuteIndexingWithEmptySiteModels() {
      List<SiteModel> siteModels = Collections.emptyList();
      when(siteHandler.getIndexedSiteModelFromSites(any())).thenReturn(siteModels);
      when(entitySaverTemplate.saveEntities(siteModels)).thenReturn(siteModels);

      executor.executeIndexing();

      verify(processor, never()).processSiteIndexing(any(SiteModel.class));
    }

    @Test
    public void testExecuteIndexingHandlesNullReturnFromSiteHandler() {
      when(siteHandler.getIndexedSiteModelFromSites(any())).thenReturn(null);

      executor.executeIndexing();

      verify(processor, never()).processSiteIndexing(any(SiteModel.class));
    }

    //    @Test
    //    public void testExecuteIndexingHandlesInterruptionsGracefully() {
    //      List<SiteModel> siteModels = List.of(new SiteModel(), new SiteModel());
    //      when(siteHandler.getIndexedSiteModelFromSites(any())).thenReturn(siteModels);
    //      when(entitySaverTemplate.saveEntities(siteModels)).thenReturn(siteModels);
    //
    //      Thread.currentThread().interrupt(); // Simulate interruption
    //
    //      executor.executeIndexing();
    //
    //      assertTrue(Thread.interrupted()); // Ensure the interrupted status is restored
    //      verify(processor, times(2)).processSiteIndexing(any(SiteModel.class));
    //    }
    //  }

    @Nested
    class GetFuturesForSiteModelsTest {

      //      @Test
      //      public void testGetFuturesForSiteModelsReturnsCompletableFutures() {
      //        List<SiteModel> siteModels = List.of(new SiteModel(), new SiteModel());
      //        when(siteHandler.getIndexedSiteModelFromSites(any())).thenReturn(siteModels);
      //        when(entitySaverTemplate.saveEntities(siteModels)).thenReturn(siteModels);
      //
      //        Collection<CompletableFuture<Void>> futures = executor.getFuturesForSiteModels();
      //
      //        assertNotNull(futures);
      //        assertEquals(siteModels.size(), futures.size());
      //        assertTrue(futures.stream().allMatch(future -> future instanceof
      // CompletableFuture));
      //      }

      //      @Nested
      //      class GetSiteModelsTest {

      //        @Test
      //        public void testGetSiteModelsRetrievesSiteModelsFromSiteHandler() {
      //          List<Site> sites =
      //              List.of(new Site("site1", "http://site1.com"), new Site("site2",
      // "http://site2.com"));
      //          when(sitesList.getSites()).thenReturn(sites);
      //
      //          SiteModel siteModel1 = new SiteModel();
      //          SiteModel siteModel2 = new SiteModel();
      //          Collection<SiteModel> siteModels = List.of(siteModel1, siteModel2);
      //          when(siteHandler.getIndexedSiteModelFromSites(sites)).thenReturn(siteModels);
      //
      //          Collection<SiteModel> result = executor.getSiteModels();
      //
      //          assertEquals(2, result.size());
      //          assertTrue(result.contains(siteModel1));
      //          assertTrue(result.contains(siteModel2));
      //        }
      //      }

      //      @Nested
      //      class GetFutureProcessTest {
      //
      ////        @Test
      ////        public void testGetFutureProcessCreatesCompletableFutureForProcessingSiteModel() {
      ////          SiteModel siteModel = new SiteModel();
      ////          CompletableFuture<Void> future = executor.getFutureProcess(siteModel);
      ////
      ////          assertNotNull(future, "The future should not be null");
      ////          assertInstanceOf(
      ////              CompletableFuture.class,
      ////              future,
      ////              "The future should be an instance of CompletableFuture");
      ////        }
      //      }
    }
  }
}
