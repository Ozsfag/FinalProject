 package searchengine.utils.entityFactory;

 import org.junit.jupiter.api.Nested;
 import org.junit.jupiter.api.Test;
 import org.mockito.Mockito;
 import searchengine.dto.indexing.ConnectionResponse;
 import searchengine.dto.indexing.Site;
 import searchengine.model.*;
 import searchengine.utils.entityFactory.impl.EntityFactoryImpl;
 import searchengine.utils.webScraper.WebScraper;
 import searchengine.utils.webScraper.impl.WebScraperImpl;

 import java.util.Collections;
 import java.util.Date;

 import static org.junit.jupiter.api.Assertions.*;
 import static org.mockito.Mockito.*;

 @Nested
 class EntityFactoryTest {
  private EntityFactory factory;

     // Successfully creates a SiteModel with default status INDEXING
     @Test
     public void test_create_site_model_with_default_status() {
         Site site = Site.builder().url("http://example.com").name("Example").build();

         SiteModel siteModel = factory.createSiteModel(site);

         assertNotNull(siteModel);
         assertEquals(Status.INDEXING, siteModel.getStatus());
         assertEquals("http://example.com", siteModel.getUrl());
         assertEquals("Example", siteModel.getName());
         assertNotNull(siteModel.getStatusTime());
         assertEquals("", siteModel.getLastError());
     }

     // Correctly maps the URL from Site to SiteModel
     @Test
     public void test_create_site_model_correctly_maps_url() {
         Site site = Site.builder()
                 .url("http://example.com")
                 .name("Example")
                 .build();


         SiteModel siteModel = factory.createSiteModel(site);

         assertNotNull(siteModel);
         assertEquals(Status.INDEXING, siteModel.getStatus());
         assertEquals("http://example.com", siteModel.getUrl());
         assertEquals("Example", siteModel.getName());
         assertNotNull(siteModel.getStatusTime());
         assertEquals("", siteModel.getLastError());
     }

     // Correctly maps the name from Site to SiteModel

     @Test
     public void test_correctly_maps_name() {
         Site site = Site.builder().url("http://testsite.com").name("Test Site").build();
         SiteModel siteModel = factory.createSiteModel(site);

         assertNotNull(siteModel);
         assertEquals(Status.INDEXING, siteModel.getStatus());
         assertEquals("http://testsite.com", siteModel.getUrl());
         assertEquals("Test Site", siteModel.getName());
         assertNotNull(siteModel.getStatusTime());
         assertEquals("", siteModel.getLastError());
     }
     // Sets the statusTime to the current date and time
     @Test
     public void test_create_site_model_sets_status_time_to_current_date() {
         Site site = Site.builder().url("http://example.com").name("Example").build();
         SiteModel siteModel = factory.createSiteModel(site);

         assertNotNull(siteModel);
         assertEquals(Status.INDEXING, siteModel.getStatus());
         assertEquals("http://example.com", siteModel.getUrl());
         assertEquals("Example", siteModel.getName());
         assertNotNull(siteModel.getStatusTime());
         assertEquals("", siteModel.getLastError());
     }

     // Sets the lastError to an empty string
     @Test
     public void test_create_site_model_sets_last_error_to_empty_string() {
         Site site = Site.builder().url("http://example.com").name("Example").build();
         SiteModel siteModel = factory.createSiteModel(site);

         assertNotNull(siteModel);
         assertEquals(Status.INDEXING, siteModel.getStatus());
         assertEquals("http://example.com", siteModel.getUrl());
         assertEquals("Example", siteModel.getName());
         assertNotNull(siteModel.getStatusTime());
         assertEquals("", siteModel.getLastError());
     }

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
     public void test_createSiteModel_handles_null_Site_input() {
         EntityFactoryImpl entityFactory = new EntityFactoryImpl(null);
         assertThrows(NullPointerException.class, () -> {
             entityFactory.createSiteModel(null);
         });
     }






     @Test
     public void test_create_site_model_with_null_site_input() {
         // Setup
         EntityFactoryImpl entityFactory = new EntityFactoryImpl(null);

         // Execution
         SiteModel siteModel = entityFactory.createSiteModel(null);

         // Verification
         assertNotNull(siteModel);
         assertEquals(Status.INDEXING, siteModel.getStatus());
         assertNull(siteModel.getUrl());
         assertNotNull(siteModel.getStatusTime());
         assertEquals("", siteModel.getLastError());
         assertNull(siteModel.getName());
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
 }
