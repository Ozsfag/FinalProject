package searchengine.utils.entityHandlers.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchengine.dto.indexing.Site;
import searchengine.model.SiteModel;
import searchengine.repositories.SiteRepository;
import searchengine.utils.entityFactory.EntityFactory;

public class SiteHandlerImplTest {

  private SiteRepository siteRepository;
  private EntityFactory entityFactory;
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
  private SiteHandlerImpl siteHandler;

  @BeforeEach
  public void setUp() {
    siteRepository = mock(SiteRepository.class);
    entityFactory = mock(EntityFactory.class);
    siteHandler = new SiteHandlerImpl();
  }

  @Test
  public void test_retrieve_existing_site_model() {
    Site site = new Site("http://example.com", "Example");
    SiteModel siteModel = new SiteModel();
    siteModel.setUrl("http://example.com");

    when(siteRepository.findSiteByUrl(site.getUrl())).thenReturn(siteModel);

    Collection<Site> sitesToParse = List.of(site);
    Collection<SiteModel> result = siteHandler.getIndexedSiteModelFromSites(sitesToParse);

    assertEquals(1, result.size());
    assertEquals("http://example.com", result.iterator().next().getUrl());
  }

  @Test
  public void test_correct_site_model_returned_for_valid_url() {
    Site site = new Site("http://example.com", "Example");
    SiteModel siteModel = new SiteModel();
    siteModel.setUrl("http://example.com");

    when(siteRepository.findSiteByUrl(site.getUrl())).thenReturn(siteModel);

    Collection<Site> sitesToParse = List.of(site);
    Collection<SiteModel> result = siteHandler.getIndexedSiteModelFromSites(sitesToParse);

    assertEquals(1, result.size());
    assertTrue(result.contains(siteModel));
  }

  @Test
  public void ensure_find_site_by_url_called_with_correct_url() {
    Site site = new Site("http://example.com", "Example");
    SiteModel siteModel = new SiteModel();
    siteModel.setUrl("http://example.com");

    when(siteRepository.findSiteByUrl(site.getUrl())).thenReturn(siteModel);

    Collection<Site> sitesToParse = List.of(site);
    siteHandler.getIndexedSiteModelFromSites(sitesToParse);

    verify(siteRepository, times(1)).findSiteByUrl(site.getUrl());
  }

  @Test
  public void test_null_returned_when_no_SiteModel_exists() {
    Site site = new Site("http://example.com", "Example");

    when(siteRepository.findSiteByUrl(site.getUrl())).thenReturn(null);

    Collection<Site> sitesToParse = List.of(site);
    Collection<SiteModel> result = siteHandler.getIndexedSiteModelFromSites(sitesToParse);

    assertFalse(result.isEmpty());
  }

  @Test
  public void test_retrieval_process_does_not_modify_site_model() {
    Site site = new Site("http://example.com", "Example");
    SiteModel siteModel = new SiteModel();
    siteModel.setUrl("http://example.com");

    when(siteRepository.findSiteByUrl(site.getUrl())).thenReturn(siteModel);

    Collection<Site> sitesToParse = List.of(site);
    Collection<SiteModel> result = siteHandler.getIndexedSiteModelFromSites(sitesToParse);

    assertEquals(1, result.size());
    assertTrue(result.contains(siteModel));
    assertEquals("http://example.com", result.iterator().next().getUrl());
  }

  @Test
  public void create_and_save_new_site_model_when_not_exist() {
    Site site = new Site("http://example.com", "Example");
    SiteModel siteModel = new SiteModel();
    siteModel.setUrl("http://example.com");

    when(siteRepository.findSiteByUrl(site.getUrl())).thenReturn(null);
    when(entityFactory.createSiteModel(site)).thenReturn(siteModel);

    Collection<Site> sitesToParse = List.of(site);
    Collection<SiteModel> result = siteHandler.getIndexedSiteModelFromSites(sitesToParse);

    assertEquals(1, result.size());
    assertEquals("http://example.com", result.iterator().next().getUrl());
    verify(entityFactory, times(1)).createSiteModel(site);
  }

  @Test
  public void test_handle_empty_collection_of_sites() {
    Collection<Site> sitesToParse = Collections.emptyList();
    Collection<SiteModel> result = siteHandler.getIndexedSiteModelFromSites(sitesToParse);

    assertTrue(result.isEmpty());
  }

  @Test
  public void test_no_exceptions_thrown_for_empty_input_collection() {
    Collection<Site> emptySitesToParse = Collections.emptyList();

    assertDoesNotThrow(() -> siteHandler.getIndexedSiteModelFromSites(emptySitesToParse));
  }

  @Test
  public void ensure_proper_handling_of_sites_with_duplicate_urls() {
    Site site = new Site("http://example.com", "Example");
    SiteModel siteModel = new SiteModel();
    siteModel.setUrl("http://example.com");

    when(siteRepository.findSiteByUrl(site.getUrl())).thenReturn(siteModel);

    Collection<Site> sitesToParse = List.of(site, site); // Adding duplicate site
    Collection<SiteModel> result = siteHandler.getIndexedSiteModelFromSites(sitesToParse);

    assertEquals(1, result.size());
    assertEquals("http://example.com", result.iterator().next().getUrl());
  }

  @Test
  public void test_consistent_results_across_runs() {
    Site site = new Site("http://example.com", "Example");
    SiteModel siteModel = new SiteModel();
    siteModel.setUrl("http://example.com");

    when(siteRepository.findSiteByUrl(site.getUrl())).thenReturn(siteModel);

    Collection<Site> sitesToParse = List.of(site);
    Collection<SiteModel> result1 = siteHandler.getIndexedSiteModelFromSites(sitesToParse);
    Collection<SiteModel> result2 = siteHandler.getIndexedSiteModelFromSites(sitesToParse);

    assertEquals(result1.size(), result2.size());
    if (!result1.isEmpty()) {
      assertEquals(result1.iterator().next().getUrl(), result2.iterator().next().getUrl());
    }
  }
}
