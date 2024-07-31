package searchengine.utils.entityHandler;

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

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class EntityHandlerTest {

    @Mock
    private SiteRepository siteRepository;
    @Mock
    private LemmaRepository lemmaRepository;
    @Mock
    private IndexRepository indexRepository;
    @InjectMocks
    public Morphology morphology;
    @Mock
    private PageRepository pageRepository;
    @InjectMocks
    private EntityFactory entityFactory;
    @InjectMocks
    private EntityHandler entityHandler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        morphology = Mockito.mock(Morphology.class);
        entityFactory = Mockito.mock(EntityFactory.class);

        entityHandler = new EntityHandler(siteRepository, lemmaRepository, indexRepository, morphology, pageRepository, entityFactory);
    }

    @Test
    public void testGetIndexedSiteModelFromSites_SingleSite() {
        List<Site> sitesToParse = List.of(
                new Site("https://example.com", "exemple"),
                new Site("https://example1.com", "exemple1")
        );
        List<SiteModel> expected = List.of(
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
                        .build()
        );

        when(siteRepository.findSiteByUrl(sitesToParse.get(0).getUrl())).thenReturn(expected.get(0));
        when(siteRepository.findSiteByUrl(sitesToParse.get(1).getUrl())).thenReturn(expected.get(1));

        Collection<SiteModel> result = entityHandler.getIndexedSiteModelFromSites(sitesToParse);

        assertEquals(expected, result);
        verify(siteRepository, times(2)).findSiteByUrl(anyString());
    }

    @Test
    public void testGetIndexedSiteModelFromSites_WhenSiteIsNotInConfiguration() {
        Site site = new Site(
                "https://example.com",
                "exemple");
        SiteModel siteModel = SiteModel.builder()
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

        List<SiteModel> result = (List<SiteModel>) entityHandler.getIndexedSiteModelFromSites(Collections.singletonList(site));

        assertEquals(siteModel, result.get(0));
        verify(siteRepository, times(1)).findSiteByUrl(anyString());
        verify(entityFactory, times(1)).createSiteModel(site);
    }
//
//    @Test
//    public void testGetIndexedSiteModelFromSites_MultipleSites() {
//        Site site1 = new Site("https://example.com");
//        SiteModel siteModel1 = new SiteModel(site1.getUrl());
//
//        Site site2 = new Site("https://example.org");
//        SiteModel siteModel2 = new SiteModel(site2.getUrl());
//
//        List<Site> sitesToParse = new ArrayList<>();
//        sitesToParse.add(site1);
//        sitesToParse.add(site2);
//
//        List<SiteModel> expected = new ArrayList<>();
//        expected.add(siteModel1);
//        expected.add(siteModel2);
//
//        when(siteRepository.findSiteByUrl(site1.getUrl())).thenReturn(Optional.empty());
//        when(entityFactory.createSiteModel(site1)).thenReturn(siteModel1);
//
//        when(siteRepository.findSiteByUrl(site2.getUrl())).thenReturn(Optional.empty());
//        when(entityFactory.createSiteModel(site2)).thenReturn(siteModel2);
//
//        List<SiteModel> result = entityHandler.getIndexedSiteModelFromSites(sitesToParse);
//
//        assertEquals(expected, result);
//    }
}
