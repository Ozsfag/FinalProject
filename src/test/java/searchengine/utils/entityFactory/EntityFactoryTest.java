 package searchengine.utils.entityFactory;

 import org.junit.jupiter.api.Nested;
 import org.junit.jupiter.api.Test;
 import searchengine.dto.indexing.ConnectionResponse;
 import searchengine.dto.indexing.Site;
 import searchengine.model.*;
 import searchengine.utils.entityFactory.impl.EntityFactoryImpl;
 import searchengine.utils.webScraper.WebScraper;

 import java.util.Collections;

 import static org.junit.jupiter.api.Assertions.*;
 import static org.mockito.Mockito.*;

 @Nested
 class EntityFactoryTest {
  private EntityFactory factory;

     @Test
     public void test_createSiteModel_returns_correct_SiteModel() {
         Site site = Site.builder()
                 .url("http://example.com")
                 .name("Example Site")
                 .build();
         EntityFactoryImpl entityFactory = new EntityFactoryImpl(null);
         SiteModel siteModel = entityFactory.createSiteModel(site);

         assertNotNull(siteModel);
         assertEquals(Status.INDEXING, siteModel.getStatus());
         assertEquals("http://example.com", siteModel.getUrl());
         assertNotNull(siteModel.getStatusTime());
         assertEquals("", siteModel.getLastError());
         assertEquals("Example Site", siteModel.getName());
     }
     @Test
     public void test_createSiteModel_handles_null_Site_input() {
         EntityFactoryImpl entityFactory = new EntityFactoryImpl(null);
         assertThrows(NullPointerException.class, () -> entityFactory.createSiteModel(null));
     }

     @Test
     public void test_create_page_model_calls_web_scraper_with_correct_path() {
         // Setup
         SiteModel siteModel = SiteModel.builder().build();
         String path = "http://example.com";
         ConnectionResponse connectionResponse = ConnectionResponse.builder()
                 .path(path)
                 .responseCode(200)
                 .content("Example content")
                 .urls(Collections.emptyList())
                 .errorMessage("")
                 .title("Example title")
                 .build();
         WebScraper webScraper = mock(WebScraper.class);
         when(webScraper.getConnectionResponse(path)).thenReturn(connectionResponse);

         EntityFactoryImpl entityFactory = new EntityFactoryImpl(webScraper);

         // Execute
         PageModel pageModel = entityFactory.createPageModel(siteModel, path);

         // Verify
         verify(webScraper).getConnectionResponse(path);
         assertEquals(siteModel, pageModel.getSite());
         assertEquals(path, pageModel.getPath());
         assertEquals(200, pageModel.getCode());
         assertEquals("Example content", pageModel.getContent());
     }














     @Test
     public void test_create_lemma_model_with_null_site_model() {
         // Prepare
         EntityFactoryImpl entityFactory = new EntityFactoryImpl(null);

         // Execute
         LemmaModel result = entityFactory.createLemmaModel(null, "test_lemma", 5);

         // Verify
         assertNotNull(result);
         assertNull(result.getSite());
         assertEquals("test_lemma", result.getLemma());
         assertEquals(5, result.getFrequency());
     }
     @Test
     public void test_create_index_model_with_null_page_model() {
         // Prepare
         SiteModel siteModel = SiteModel.builder().build();
         LemmaModel lemmaModel = LemmaModel.builder().build();
         Float ranking = 0.5f;
         EntityFactoryImpl entityFactory = new EntityFactoryImpl(null);

         // Execute
         IndexModel indexModel = entityFactory.createIndexModel(null, lemmaModel, ranking);

         // Verify
         assertNull(indexModel.getPage());
         assertSame(lemmaModel, indexModel.getLemma());
         assertEquals(ranking, indexModel.getRank());
     }









     // Handling WebScraper returning null ConnectionResponse
     @Test
     public void test_handling_web_scraper_returning_null_connection_response() {
         // Prepare
         Site site = Site.builder()
                 .url("http://example.com")
                 .name("Example Site")
                 .build();
         WebScraper webScraper = mock(WebScraper.class);
         when(webScraper.getConnectionResponse(anyString())).thenReturn(null);

         EntityFactoryImpl entityFactory = new EntityFactoryImpl(webScraper);

         // Verify
         assertThrows(NullPointerException.class, () -> entityFactory.createPageModel(entityFactory.createSiteModel(site), "http://example.com/page"));
     }
     // Verifying SiteModel creation when Site has empty fields
     @Test
     public void test_create_site_model_with_empty_fields() {
         Site site = Site.builder().build();

         WebScraper webScraper = mock(WebScraper.class);
         when(webScraper.getConnectionResponse(anyString())).thenReturn(new ConnectionResponse());

         EntityFactoryImpl entityFactory = new EntityFactoryImpl(webScraper);
         SiteModel siteModel = entityFactory.createSiteModel(site);

         assertNotNull(siteModel);
         assertNull(siteModel.getUrl());
         assertNull(siteModel.getName());
         assertEquals(Status.INDEXING, siteModel.getStatus());
         assertNotNull(siteModel.getStatusTime());
         assertEquals("", siteModel.getLastError());
     }
     // Verifying LemmaModel creation with zero frequency
     @Test
     public void test_create_lemma_model_with_zero_frequency() {
         // Prepare
         SiteModel site = SiteModel.builder()
                 .url("http://example.com")
                 .name("Example Site")
                 .build();
         EntityFactoryImpl entityFactory = new EntityFactoryImpl(null);

         // Test
         LemmaModel lemmaModel = entityFactory.createLemmaModel(site, "test_lemma", 0);

         // Verify
         assertNotNull(lemmaModel);
         assertEquals(site, lemmaModel.getSite());
         assertEquals("test_lemma", lemmaModel.getLemma());
         assertEquals(0, lemmaModel.getFrequency());
     }
 }
