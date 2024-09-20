package searchengine.utils.indexing.procesor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchengine.model.SiteModel;
import searchengine.utils.indexing.processor.impl.ProcessorImpl;
import searchengine.utils.indexing.processor.taskFactory.TaskFactory;
import searchengine.utils.indexing.processor.updater.siteUpdater.SiteUpdater;

public class ProcessorImplTest {

    private ForkJoinPool forkJoinPool;
    private TaskFactory taskFactory;
    private SiteUpdater siteUpdater;
    private SiteModel siteModel;
    private ProcessorImpl processor;
    private ForkJoinTask task;

    @BeforeEach
    public void setUp() {
        forkJoinPool = mock(ForkJoinPool.class);
        taskFactory = mock(TaskFactory.class);
        siteUpdater = mock(SiteUpdater.class);
        siteModel = new SiteModel();
        siteModel.setUrl("http://example.com");
        processor = new ProcessorImpl(forkJoinPool, taskFactory, siteUpdater);
        task = mock(ForkJoinTask.class);
        when(taskFactory.initTask(siteModel, siteModel.getUrl())).thenReturn(task);
    }

    @Test
    public void testSuccessfulSiteIndexing() {
        // Act
        processor.processSiteIndexing(siteModel);

        // Assert
        verify(task).fork();
        verify(task).join();
        verify(siteUpdater).updateSiteWhenSuccessful(siteModel);
    }

    @Test
    public void testHandleTaskExecutionException() {
        // Arrange
        doThrow(new Error("Task execution failed")).when(task).join();

        // Act
        processor.processSiteIndexing(siteModel);

        // Assert
        verify(task).fork();
        verify(task).join();
        verify(siteUpdater).updateSiteWhenFailed(eq(siteModel), any(Error.class));
    }

    @Test
    public void testProcessSiteWithNullUrl() {
        // Arrange
        siteModel.setUrl(null);
        when(taskFactory.initTask(siteModel, null)).thenReturn(task);

        // Act
        processor.processSiteIndexing(siteModel);

        // Assert
        verify(task).fork();
        verify(task).join();
        verify(siteUpdater).updateSiteWhenSuccessful(siteModel);
        assertNull(siteModel.getUrl(), "The URL of the site model should remain null");
    }

    @Test
    public void testTaskJoinCalledAfterForking() {
        // Act
        processor.processSiteIndexing(siteModel);

        // Assert
        verify(task).fork();
        verify(task).join();
    }

    @Test
    public void testTaskNotNullAfterInitialization() {
        // Assert
        assertNotNull(task);
    }

    @Test
    public void testUrlMatchingInitTask() {
        // Act
        processor.processSiteIndexing(siteModel);

        // Assert
        verify(taskFactory).initTask(siteModel, siteModel.getUrl());
    }
}