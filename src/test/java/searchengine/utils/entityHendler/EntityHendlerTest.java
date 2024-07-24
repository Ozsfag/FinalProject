package searchengine.utils.entityHendler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import searchengine.config.SitesList;
import searchengine.dto.indexing.Site;
import searchengine.exceptions.OutOfSitesConfigurationException;
import searchengine.model.SiteModel;
import searchengine.repositories.SiteRepository;
import searchengine.utils.entityHandler.EntityHandler;
import searchengine.utils.morphology.Morphology;

import java.net.URISyntaxException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntityHandlerTest {
    @InjectMocks
    private Morphology morphology;
    @InjectMocks
    private SitesList sitesList;
    private SiteRepository siteRepository;
    @InjectMocks
    private EntityHandler entityHandler;

    @BeforeEach
    public  void setUp() {
        morphology = Mockito.mock(Morphology.class);
        sitesList = Mockito.mock(SitesList.class);
        siteRepository = Mockito.mock(SiteRepository.class);
        entityHandler = Mockito.mock(EntityHandler.class);
    }

    @Test
    void getIndexedSiteModel_validUrl_returnsSiteModel() throws URISyntaxException {
        // Arrange
        String href = "https://example.com";
        String validatedUrl = "https://example.com";
        Site site = new Site("https://example.com", "Example Site");
        SiteModel siteModel = new SiteModel();

        when(morphology.getValidUrlComponents(href)).thenReturn(new String[]{validatedUrl});
        when(sitesList.getSites()).thenReturn(Collections.singletonList(site));
        when(siteRepository.findByUrl(validatedUrl)).thenReturn(null);
        when(siteRepository.saveAndFlush(any(SiteModel.class))).thenReturn(siteModel);

        // Assert
        assertThrows(RuntimeException.class, (Executable) entityHandler.getIndexedSiteModel(href));
        verify(morphology, times(1)).getValidUrlComponents(href);
        verify(sitesList, times(1)).getSites();
        verify(siteRepository, times(1)).findByUrl(validatedUrl);
        verify(siteRepository, times(1)).saveAndFlush(any(SiteModel.class));
    }

    @Test
    void getIndexedSiteModel_invalidUrl_throwsOutOfSitesConfigurationException() throws URISyntaxException {
        // Arrange
        String href = "https://examples.com";
        String validatedUrl = "https://example.com";
        Site site = new Site("https://example.com", "Example Site");

        when(morphology.getValidUrlComponents(href)).thenReturn(new String[]{validatedUrl});
        when(sitesList.getSites()).thenReturn(Collections.singletonList(site));
        when(siteRepository.findByUrl(validatedUrl)).thenReturn(null);

        // Act & Assert
        assertThrows(OutOfSitesConfigurationException.class, () -> entityHandler.getIndexedSiteModel(href));
    }

    @Test
    void getIndexedSiteModel_URISyntaxException_throwsRuntimeException() throws URISyntaxException {
        // Arrange
        String href = "https://example.com";

        when(morphology.getValidUrlComponents(href)).thenThrow(new URISyntaxException("Invalid URL", href));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> entityHandler.getIndexedSiteModel(href));
    }
}
