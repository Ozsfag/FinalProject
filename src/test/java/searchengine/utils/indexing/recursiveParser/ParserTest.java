// package searchengine.utils.indexing.parser;
//
// import org.junit.Before;
// import org.junit.Test;
// import org.mockito.ArgumentCaptor;
// import org.mockito.InOrder;
// import searchengine.model.SiteModel;
// import searchengine.repositories.SiteRepository;
// import searchengine.utils.indexing.IndexingStrategy;
// import searchengine.utils.indexing.processor.taskFactory.TaskFactory;
// import searchengine.utils.urlsHandler.UrlsChecker;
//
// import java.util.*;
// import java.util.concurrent.ForkJoinTask;
//
// import static org.junit.Assert.*;
// import static org.mockito.Mockito.*;
//
// public class ParserTest {
//
//    private UrlsChecker urlsChecker;
//    private IndexingStrategy indexingStrategy;
//    private SiteRepository siteRepository;
//    private TaskFactory taskFactory;
//    private Parser parser;
//    private SiteModel siteModel;
//    private String href;
//
//    @Before
//    public void setUp() {
//        urlsChecker = mock(UrlsChecker.class);
//        indexingStrategy = mock(IndexingStrategy.class);
//        siteRepository = mock(SiteRepository.class);
//        taskFactory = mock(TaskFactory.class);
//        parser = new Parser(urlsChecker, indexingStrategy, siteRepository, taskFactory);
//        siteModel = new SiteModel();
//        href = "http://example.com";
//    }
//
//    @Test
//    public void test_initializes_parser_with_site_model_and_url() {
//        parser.init(siteModel, href);
//        assertEquals(siteModel, parser.getSiteModel());
//        assertEquals(href, parser.getHref());
//    }
//
//    @Test
//    public void test_no_urls_to_parse_after_checking() {
//        when(urlsChecker.getCheckedUrls(href, siteModel)).thenReturn(Collections.emptyList());
//
//        parser.init(siteModel, href);
//        Boolean result = parser.compute();
//
//        assertTrue(result);
//        verify(indexingStrategy, never()).processIndexing(anyCollection(), any(SiteModel.class));
//        verify(siteRepository, never()).updateStatusTimeByUrl(any(Date.class), anyString());
//        verify(taskFactory, never()).initTask(any(SiteModel.class), anyString());
//    }
//
//    @Test
//    public void test_site_status_time_updated_in_repository() {
//        // Arrange
//        when(urlsChecker.getCheckedUrls(href, siteModel)).thenReturn(Collections.emptyList());
//        parser.init(siteModel, href);
//
//        // Act
//        parser.compute();
//
//        // Assert
//        ArgumentCaptor<Date> dateCaptor = ArgumentCaptor.forClass(Date.class);
//        verify(siteRepository).updateStatusTimeByUrl(dateCaptor.capture(), eq(href));
//
//        Date capturedDate = dateCaptor.getValue();
//        assertNotNull(capturedDate);
//        assertTrue(capturedDate.getTime() <= new Date().getTime());
//    }
//
//    @Test
//    public void test_returns_true_after_successful_computation() {
//        when(urlsChecker.getCheckedUrls(href, siteModel)).thenReturn(Arrays.asList("url1",
// "url2"));
//        when(taskFactory.initTask(any(SiteModel.class),
// anyString())).thenReturn(mock(ForkJoinTask.class));
//
//        parser.init(siteModel, href);
//        Boolean result = parser.compute();
//
//        assertTrue(result);
//    }
//
//    @Test
//    public void test_return_value_not_affected_by_exceptions_in_subtasks() {
//        when(urlsChecker.getCheckedUrls(anyString(),
// any(SiteModel.class))).thenReturn(Arrays.asList("url1", "url2"));
//        when(taskFactory.initTask(any(SiteModel.class),
// anyString())).thenReturn(mock(ForkJoinTask.class));
//
//        parser.init(siteModel, href);
//        Boolean result = parser.compute();
//
//        assertTrue(result);
//    }
//
//    @Test
//    public void test_retrieves_checked_urls() {
//        Collection<String> checkedUrls = Arrays.asList("https://example.com/page1",
// "https://example.com/page2");
//        when(urlsChecker.getCheckedUrls(href, siteModel)).thenReturn(checkedUrls);
//
//        parser.init(siteModel, href);
//        parser.compute();
//
//        verify(urlsChecker).getCheckedUrls(href, siteModel);
//    }
//
//    @Test
//    public void test_processes_indexing() {
//        Collection<String> checkedUrls = Arrays.asList("https://example.com/page1",
// "https://example.com/page2");
//        when(urlsChecker.getCheckedUrls(href, siteModel)).thenReturn(checkedUrls);
//
//        parser.init(siteModel, href);
//        parser.compute();
//
//        verify(indexingStrategy).processIndexing(checkedUrls, siteModel);
//    }
//
//    @Test
//    public void test_creates_and_invokes_subtasks() {
//        Collection<String> urlsToParse = Arrays.asList("https://example.com/page1",
// "https://example.com/page2");
//        when(urlsChecker.getCheckedUrls(href, siteModel)).thenReturn(urlsToParse);
//        when(taskFactory.initTask(eq(siteModel),
// anyString())).thenReturn(mock(ForkJoinTask.class));
//
//        parser.init(siteModel, href);
//        parser.compute();
//
//        verify(taskFactory, times(urlsToParse.size())).initTask(eq(siteModel), anyString());
//    }
//
//    @Test
//    public void test_site_repository_fails_to_update_status_time() {
//        when(urlsChecker.getCheckedUrls(eq(href),
// eq(siteModel))).thenReturn(Collections.emptyList());
//
//        parser.init(siteModel, href);
//        parser.compute();
//
//        verify(siteRepository, times(1)).updateStatusTimeByUrl(any(Date.class), eq(href));
//    }
//
//    @Test
//    public void test_get_checked_urls_called_before_indexing() {
//        Collection<String> checkedUrls = Arrays.asList("http://example.com/page1",
// "http://example.com/page2");
//        when(urlsChecker.getCheckedUrls(eq(href), eq(siteModel))).thenReturn(checkedUrls);
//
//        parser.init(siteModel, href);
//        parser.compute();
//
//        InOrder inOrder = inOrder(urlsChecker, indexingStrategy, siteRepository, taskFactory);
//        inOrder.verify(urlsChecker).getCheckedUrls(eq(href), eq(siteModel));
//        inOrder.verify(indexingStrategy).processIndexing(eq(checkedUrls), eq(siteModel));
//        inOrder.verify(siteRepository).updateStatusTimeByUrl(any(Date.class), eq(href));
//        inOrder.verify(taskFactory, times(checkedUrls.size())).initTask(eq(siteModel),
// anyString());
//    }
// }
