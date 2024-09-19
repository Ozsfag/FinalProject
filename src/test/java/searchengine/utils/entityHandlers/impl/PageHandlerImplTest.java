package searchengine.utils.entityHandlers.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchengine.exceptions.StoppedExecutionException;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.services.indexing.impl.IndexingImpl;
import searchengine.utils.entityFactory.EntityFactory;

public class PageHandlerImplTest {

  private EntityFactory entityFactory;
  private PageHandlerImpl pageHandler;
  private SiteModel siteModel;
  private Collection<String> urls;

  @BeforeEach
  public void setUp() {
    entityFactory = mock(EntityFactory.class);
    pageHandler = new PageHandlerImpl(entityFactory);
    siteModel = new SiteModel();
    urls = Arrays.asList("http://example.com/page1", "http://example.com/page2");
  }

  @Test
  public void test_returns_page_models_for_valid_urls() {
    mockEntityFactoryResponses(new PageModel(), new PageModel());

    Collection<PageModel> result = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertEquals(2, result.size());
  }

  @Test
  public void test_returns_page_models_for_non_empty_valid_urls() {
    mockEntityFactoryResponses(new PageModel(), new PageModel());

    Collection<PageModel> pageModels = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertNotNull(pageModels);
    assertFalse(pageModels.isEmpty());
  }

  @Test
  public void test_each_url_corresponds_to_page_model() {
    PageModel pageModel1 = PageModel.builder().path("http://example.com/page1").build();
    PageModel pageModel2 = PageModel.builder().path("http://example.com/page2").build();

    mockEntityFactoryResponses(pageModel1, pageModel2);

    Collection<PageModel> pageModels = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertNotNull(pageModels);
    assertEquals(2, pageModels.size());
    assertTrue(pageModels.containsAll(new HashSet<>(Arrays.asList(pageModel1, pageModel2))));

    verifyEntityFactoryCalls();
  }

  @Test
  public void test_output_collection_no_null_elements() {
    mockEntityFactoryResponses(new PageModel(), null);

    Collection<PageModel> pageModels = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertNotNull(pageModels);
    assertFalse(pageModels.contains(null));
  }

  @Test
  public void test_output_collection_size_matches_number_of_valid_urls() {
    mockEntityFactoryResponses(new PageModel(), new PageModel());

    Collection<PageModel> pageModels = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertNotNull(pageModels);
    assertEquals(2, pageModels.size());

    verifyEntityFactoryCalls();
  }

  @Test
  public void test_successfully_uses_entity_factory_for_page_models() {
    mockEntityFactoryResponses(new PageModel(), new PageModel());

    Collection<PageModel> pageModels = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertEquals(2, pageModels.size());
    verifyEntityFactoryCalls();
  }

  @Test
  public void test_calls_entity_factory_for_each_url() {
    mockEntityFactoryResponses(new PageModel(), new PageModel());

    Collection<PageModel> pageModels = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertEquals(2, pageModels.size());
    verifyEntityFactoryCalls();
  }

  @Test
  public void test_correctly_uses_entity_factory_for_page_model_creation() {
    mockEntityFactoryResponses(new PageModel(), new PageModel());

    Collection<PageModel> pageModels = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertEquals(2, pageModels.size());
    verifyEntityFactoryCalls();
  }

  @Test
  public void test_create_page_model_returns_valid_page_models() {
    mockEntityFactoryResponses(new PageModel(), new PageModel());

    Collection<PageModel> pageModels = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertEquals(2, pageModels.size());
    verifyEntityFactoryCalls();
  }

  @Test
  public void test_handles_null_from_create_page_model() {
    mockEntityFactoryResponses(new PageModel(), null);

    Collection<PageModel> pageModels = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertEquals(1, pageModels.size());
    verifyEntityFactoryCalls();
  }

  @Test
  public void initializes_fields_with_arguments() {
    mockEntityFactoryResponses(new PageModel(), new PageModel());

    Collection<PageModel> result = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertEquals(2, result.size());
  }

  @Test
  public void assigns_urls_to_parse_field_with_provided_collection_of_urls() {
    pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertEquals(urls, pageHandler.getUrlsToParse());
  }

  @Test
  public void assigns_site_model_field_with_provided_object() {
    mockEntityFactoryResponses(new PageModel(), new PageModel());

    Collection<PageModel> pageModels = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertEquals(2, pageModels.size());
    verifyEntityFactoryCalls();
  }

  @Test
  public void test_correctly_updates_fields_before_processing_urls() {
    siteModel.setId(1);
    siteModel.setUrl("http://example.com");
    siteModel.setName("Example Site");

    PageModel pageModel1 = new PageModel();
    PageModel pageModel2 = new PageModel();

    mockEntityFactoryResponses(pageModel1, pageModel2);

    Collection<PageModel> result = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertEquals(new HashSet<>(Arrays.asList(pageModel1, pageModel2)), result);
  }

  @Test
  public void test_retain_field_values_after_processing() {
    mockEntityFactoryResponses(new PageModel(), new PageModel());

    Collection<PageModel> pageModels = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertEquals(2, pageModels.size());
    assertEquals(urls, pageHandler.getUrlsToParse());
    assertEquals(siteModel, pageHandler.getSiteModel());
  }

  @Test
  public void test_resulting_collection_contains_non_null_page_models() {
    mockEntityFactoryResponses(new PageModel(), null);

    Collection<PageModel> result = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertTrue(result.stream().allMatch(Objects::nonNull));
  }

  @Test
  public void filters_out_null_page_models() {
    mockEntityFactoryResponses(new PageModel(), null);

    Collection<PageModel> result = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertEquals(1, result.size());
  }

  @Test
  public void handles_non_empty_collection_of_urls_appropriately() {
    mockEntityFactoryResponses(new PageModel(), new PageModel());

    Collection<PageModel> result = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertEquals(2, result.size());
  }

  @Test
  public void test_no_duplicate_page_models_returned() {
    mockEntityFactoryResponses(new PageModel(), new PageModel());

    Collection<PageModel> pageModels = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertEquals(2, pageModels.size());
    verifyEntityFactoryCalls();
  }

  @Test
  public void test_final_collection_is_set() {
    mockEntityFactoryResponses(new PageModel(), new PageModel());

    Collection<PageModel> pageModels = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertEquals(2, pageModels.size());
    verifyEntityFactoryCalls();
  }

  @Test
  public void validates_each_page_model_is_unique() {
    mockEntityFactoryResponses(new PageModel(), new PageModel());

    Collection<PageModel> pageModels = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertEquals(2, pageModels.size());
    verifyEntityFactoryCalls();
  }

  @Test
  public void test_no_duplicate_page_models_created_for_duplicate_urls() {
    // Arrange
    urls = Arrays.asList("http://example.com/page1", "http://example.com/page1");
    PageModel expectedPageModel = new PageModel();
    when(entityFactory.createPageModel(siteModel, "http://example.com/page1"))
        .thenReturn(expectedPageModel);

    // Act
    Collection<PageModel> pageModels = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    // Assert
    assertEquals(1, pageModels.size(), "The collection should contain exactly one PageModel.");
    assertTrue(
        pageModels.contains(expectedPageModel),
        "The collection should contain the expected PageModel.");
    verify(entityFactory, times(1)).createPageModel(siteModel, "http://example.com/page1");
  }

  @Test
  public void test_final_collection_size_matches_unique_urls() {
    mockEntityFactoryResponses(new PageModel(), new PageModel());

    Collection<PageModel> pageModels = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertEquals(2, pageModels.size());
    verifyEntityFactoryCalls();
  }

  @Test
  public void throws_stopped_execution_exception_when_is_indexing_false() {
    // Backup the original value of isIndexing
    boolean originalIsIndexing = IndexingImpl.isIndexing;

    try {
      // Ensure isIndexing is set to false
      IndexingImpl.isIndexing = false;

      // Setup
      PageHandlerImpl pageHandler = new PageHandlerImpl(entityFactory);
      SiteModel siteModel = new SiteModel();
      Collection<String> urls =
          Arrays.asList("http://example.com/page1", "http://example.com/page2");

      // Mocking behavior
      when(entityFactory.createPageModel(siteModel, "http://example.com/page1"))
          .thenReturn(new PageModel());
      when(entityFactory.createPageModel(siteModel, "http://example.com/page2"))
          .thenReturn(new PageModel());

      // Test
      StoppedExecutionException exception =
          assertThrows(
              StoppedExecutionException.class,
              () -> pageHandler.getIndexedPageModelsFromUrls(urls, siteModel));

      // Verify the exception message
      assertEquals("Индексация остановлена пользователем", exception.getMessage());

      // Verify that createPageModel was never called
      verify(entityFactory, never()).createPageModel(any(), any());
    } finally {
      // Reset isIndexing to its original value
      IndexingImpl.isIndexing = originalIsIndexing;
    }
  }

  @Test
  public void test_checks_isIndexing_before_processing_URLs() {
    mockEntityFactoryResponses(new PageModel(), new PageModel());

    Collection<PageModel> pageModels = pageHandler.getIndexedPageModelsFromUrls(urls, siteModel);

    assertEquals(2, pageModels.size());
    verifyEntityFactoryCalls();
  }

  private void mockEntityFactoryResponses(PageModel... pageModels) {
    for (int i = 0; i < pageModels.length; i++) {
      when(entityFactory.createPageModel(any(SiteModel.class), eq(urls.toArray()[i].toString())))
          .thenReturn(pageModels[i]);
    }
  }

  private void verifyEntityFactoryCalls() {
    for (String url : urls) {
      verify(entityFactory, times(1)).createPageModel(siteModel, url);
    }
  }
}
