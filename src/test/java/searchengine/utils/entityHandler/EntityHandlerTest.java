package searchengine.utils.entityHandler;

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
import searchengine.utils.dataTransfer.DataTransformer;
import searchengine.utils.validator.Validator;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntityHandlerTest {
    @InjectMocks
    private Validator validator;
    private SitesList sitesList;
    @InjectMocks
    private DataTransformer dataTransformer;
    private SiteRepository siteRepository;
    @InjectMocks
    private EntityHandler entityHandler;
    private Site site;
    private String href;
    String validatedUrl;

    @BeforeEach
    public  void setUp() {
        validator = Mockito.mock(Validator.class);
        siteRepository = Mockito.mock(SiteRepository.class);
        entityHandler = Mockito.mock(EntityHandler.class);
        site = Site.builder().url("https://example.com").name("Example Site").build();
        href = "https://example.com/";
        validatedUrl = "https://example.com";
        sitesList = SitesList.builder()
                .sites(List.of(site))
                .build();
    }

    @Test
    void getIndexedSiteModel_validUrl_returnsSiteModelFromUrls() throws URISyntaxException {
        // Arrange
        SiteModel siteModel = new SiteModel();

        when(validator.getValidUrlComponents(href)).thenReturn(new String[]{validatedUrl});
        when(sitesList.getSites()).thenReturn(Collections.singletonList(site));
        when(siteRepository.findSiteByUrl(validatedUrl)).thenReturn(null);
        when(siteRepository.saveAndFlush(any(SiteModel.class))).thenReturn(siteModel);

        // Assert
        assertThrows(RuntimeException.class, (Executable) entityHandler.getIndexedSiteModelFromSites(dataTransformer.transformUrlToSites(href)));
        verify(validator, times(1)).getValidUrlComponents(href);
        verify(sitesList, times(1)).getSites();
        verify(siteRepository, times(1)).findSiteByUrl(validatedUrl);
        verify(siteRepository, times(1)).saveAndFlush(any(SiteModel.class));
    }

    @Test
    void getIndexedSiteModel_FromUrls_invalidUrl_throwsOutOfSitesConfigurationException() throws URISyntaxException {
        // Arrange
        when(validator.getValidUrlComponents(href)).thenReturn(new String[]{validatedUrl});
        when(sitesList.getSites()).thenReturn(Collections.singletonList(site));
        when(siteRepository.findSiteByUrl(validatedUrl)).thenReturn(null);

        // Act & Assert
        assertThrows(OutOfSitesConfigurationException.class, () -> entityHandler.getIndexedSiteModelFromSites(dataTransformer.transformUrlToSites(href)));
    }

    @Test
    void getIndexedSiteModel_FromUrls_URISyntaxException_throwsRuntimeException() throws URISyntaxException {
        // Arrange
        String href = "https://example.com";

        when(validator.getValidUrlComponents(href)).thenThrow(new URISyntaxException("Invalid URL", href));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> entityHandler.getIndexedSiteModelFromSites(dataTransformer.transformUrlToSites(href)));
    }
}
