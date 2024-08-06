package searchengine.utils.entityHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import searchengine.dto.indexing.Site;
import searchengine.model.SiteModel;
import searchengine.model.Status;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.utils.entityFactory.EntityFactory;
import searchengine.utils.morphology.Morphology;

public class SiteHandlerTest {
  @Mock private SiteRepository siteRepository;
  @Mock private LemmaRepository lemmaRepository;
  @Mock private IndexRepository indexRepository;
  @InjectMocks public Morphology morphology;
  @Mock private PageRepository pageRepository;
  @InjectMocks private EntityFactory entityFactory;
  @InjectMocks private EntityHandler entityHandler;
  @InjectMocks private SiteHandler siteHandler;
  @InjectMocks private PageHandler pageHandler;
  @InjectMocks private LemmaHandler lemmaHandler;
  @InjectMocks private IndexHandler indexHandler;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    morphology = Mockito.mock(Morphology.class);
    entityFactory = Mockito.mock(EntityFactory.class);
    siteHandler = Mockito.mock(SiteHandler.class);
    pageHandler = Mockito.mock(PageHandler.class);
    lemmaHandler = Mockito.mock(LemmaHandler.class);
    indexHandler = Mockito.mock(IndexHandler.class);

    entityHandler =
        new EntityHandler(
            siteRepository,
            lemmaRepository,
            indexRepository,
            morphology,
            pageRepository,
            lemmaHandler,
            indexHandler,
            pageHandler);
  }

  @Test
  public void testGetIndexedSiteModelFromSites_SingleSite() {
    List<Site> sitesToParse =
        List.of(
            new Site("https://example.com", "exemple"),
            new Site("https://example1.com", "exemple1"));
    List<SiteModel> expected =
        List.of(
            SiteModel.builder()
                .id(0)
                .status(Status.INDEXING)
                .lastError("")
                .statusTime(new Date())
                .url("https://example.com")
                .name("exemple")
                .version(0)
                .build(),
            SiteModel.builder()
                .id(1)
                .status(Status.INDEXING)
                .lastError("")
                .statusTime(new Date())
                .url("https://example1.com")
                .name("exemple1")
                .version(0)
                .build());

    when(siteRepository.findSiteByUrl(sitesToParse.get(0).getUrl())).thenReturn(expected.get(0));
    when(siteRepository.findSiteByUrl(sitesToParse.get(1).getUrl())).thenReturn(expected.get(1));

    Collection<SiteModel> result = siteHandler.getIndexedSiteModelFromSites(sitesToParse);

    assertEquals(expected, result);
    verify(siteRepository, times(2)).findSiteByUrl(anyString());
  }

  @Test
  public void testGetIndexedSiteModelFromSites_WhenSiteIsNotInConfiguration() {
    Site site = new Site("https://example.com", "exemple");
    SiteModel siteModel =
        SiteModel.builder()
            .id(0)
            .status(Status.INDEXING)
            .lastError("")
            .statusTime(new Date())
            .url("https://example.com")
            .name("exemple")
            .version(0)
            .build();

    when(siteRepository.findSiteByUrl(site.getUrl())).thenReturn(null);
    when(entityFactory.createSiteModel(site)).thenReturn(siteModel);

    List<SiteModel> result =
        (List<SiteModel>) siteHandler.getIndexedSiteModelFromSites(Collections.singletonList(site));

    assertEquals(siteModel, result);
    verify(siteRepository, times(1)).findSiteByUrl(anyString());
    verify(entityFactory, times(1)).createSiteModel(site);
  }
}
