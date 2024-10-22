package searchengine.utils.entityFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import searchengine.dto.indexing.ConnectionResponse;
import searchengine.dto.indexing.Site;
import searchengine.model.*;
import searchengine.utils.entityFactory.impl.EntityFactoryImpl;
import searchengine.utils.webScraper.WebScraper;

@Nested
class EntityFactoryTest {

  private EntityFactoryImpl entityFactory;
  private WebScraper webScraper;

  @BeforeEach
  void setUp() {
    webScraper = mock(WebScraper.class);
    entityFactory = new EntityFactoryImpl();
  }

  @Test
  void test_createSiteModel_returns_correct_SiteModel() {
    Site site = Site.builder().url("http://example.com").name("Example Site").build();
    SiteModel siteModel = entityFactory.createSiteModel(site);

    assertNotNull(siteModel);
    assertEquals(Status.INDEXING, siteModel.getStatus());
    assertEquals("http://example.com", siteModel.getUrl());
    assertNotNull(siteModel.getStatusTime());
    assertEquals("", siteModel.getLastError());
    assertEquals("Example Site", siteModel.getName());
  }

  @Test
  void test_createSiteModel_handles_null_Site_input() {
    assertThrows(NullPointerException.class, () -> entityFactory.createSiteModel(null));
  }

  @Test
  void test_create_page_model_calls_web_scraper_with_correct_path() {
    SiteModel siteModel = SiteModel.builder().build();
    String path = "http://example.com";
    ConnectionResponse connectionResponse =
        new ConnectionResponse(
            Collections.emptyList(), path, "Example content", "", "Example title", 200);

    when(webScraper.getConnectionResponse(path)).thenReturn(connectionResponse);

    PageModel pageModel = entityFactory.createPageModel(siteModel, path);

    verify(webScraper).getConnectionResponse(path);
    assertEquals(siteModel, pageModel.getSite());
    assertEquals(path, pageModel.getPath());
    assertEquals(200, pageModel.getCode());
    assertEquals("Example content", pageModel.getContent());
  }

  @Test
  void test_create_lemma_model_with_null_site_model() {
    LemmaModel result = entityFactory.createLemmaModel(null, "test_lemma", 5);

    assertNotNull(result);
    assertNull(result.getSite());
    assertEquals("test_lemma", result.getLemma());
    assertEquals(5, result.getFrequency());
  }

  @Test
  void test_create_index_model_with_null_page_model() {
    SiteModel siteModel = SiteModel.builder().build();
    LemmaModel lemmaModel = LemmaModel.builder().build();
    Float ranking = 0.5f;

    IndexModel indexModel = entityFactory.createIndexModel(null, lemmaModel, ranking);

    assertNull(indexModel.getPage());
    assertSame(lemmaModel, indexModel.getLemma());
    assertEquals(ranking, indexModel.getRank());
  }

  @Test
  void test_handling_web_scraper_returning_null_connection_response() {
    Site site = Site.builder().url("http://example.com").name("Example Site").build();
    when(webScraper.getConnectionResponse(anyString())).thenReturn(null);

    assertThrows(
        NullPointerException.class,
        () ->
            entityFactory.createPageModel(
                entityFactory.createSiteModel(site), "http://example.com/page"));
  }

  @Test
  void test_create_site_model_with_empty_fields() {
    Site site = Site.builder().build();
    when(webScraper.getConnectionResponse(anyString())).thenReturn(new ConnectionResponse());

    SiteModel siteModel = entityFactory.createSiteModel(site);

    assertNotNull(siteModel);
    assertNull(siteModel.getUrl());
    assertNull(siteModel.getName());
    assertEquals(Status.INDEXING, siteModel.getStatus());
    assertNotNull(siteModel.getStatusTime());
    assertEquals("", siteModel.getLastError());
  }

  @Test
  void test_create_lemma_model_with_zero_frequency() {
    SiteModel site = SiteModel.builder().url("http://example.com").name("Example Site").build();

    LemmaModel lemmaModel = entityFactory.createLemmaModel(site, "test_lemma", 0);

    assertNotNull(lemmaModel);
    assertEquals(site, lemmaModel.getSite());
    assertEquals("test_lemma", lemmaModel.getLemma());
    assertEquals(0, lemmaModel.getFrequency());
  }
}
