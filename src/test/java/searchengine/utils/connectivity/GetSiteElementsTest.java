package searchengine.utils.connectivity;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import searchengine.config.ConnectionSettings;
import searchengine.dto.indexing.responseImpl.ConnectionResponse;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GetSiteElementsTest {

    private GetSiteElements getSiteElements;
    private ConnectionSettings connectionSettings;
    private Document document;

    @BeforeEach
    public void setup() {
        connectionSettings = Mockito.mock(ConnectionSettings.class);
        getSiteElements = new GetSiteElements(connectionSettings);
        document = mock(Document.class);
    }

    @Test
    public void testGetConnectionResponse_Success() {
        String url = "http://example.com";
        String content = "This is the content";
        Set<String> urls = Set.of("http://example.com/page1", "http://example.com/page2");


        when(connectionSettings.getUserAgent()).thenReturn("User Agent");
        when(connectionSettings.getReferrer()).thenReturn("Referrer");


        when(document.body().text()).thenReturn(content);
        when(document.select("a[href]")).thenReturn(mock(Elements.class));
        when(document.select("a[href]").stream()).thenReturn((Stream<Element>) urls);
        when(document.select("a[href]").stream().map(element -> element.absUrl("href"))).thenReturn(urls.stream());

        ConnectionResponse response = getSiteElements.getConnectionResponse(url);

        assertEquals(url, response.getPath());
        assertEquals(HttpStatus.OK.value(), response.getResponseCode());
        assertEquals(content, response.getContent());
        assertEquals(urls, response.getUrls());
        assertEquals("", response.getErrorMessage());
    }

    @Test
    public void testGetConnectionResponse_IOException() {
        String url = "http://example.com";

//        when(Jsoup.connect(url)).thenThrow(new IOException());

        ConnectionResponse response = getSiteElements.getConnectionResponse(url);

        assertEquals(url, response.getPath());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getResponseCode());
        assertEquals("", response.getContent());
        assertEquals(new HashSet<>(), response.getUrls());
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), response.getErrorMessage());
    }
}
