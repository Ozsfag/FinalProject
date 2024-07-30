package searchengine.utils.scraper;

import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import searchengine.config.ConnectionSettings;
import searchengine.dto.indexing.responseImpl.ConnectionResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class WebScraperTest {

    private WebScraper webScraper;
    private Document document;
    private final String correctUrl = "http://example.com";

    @SneakyThrows
    @BeforeEach
    public void setup() {
        ConnectionSettings connectionSettings = ConnectionSettings.builder()
                .userAgent("User Agent")
                .referrer("Referrer")
                .build();
        webScraper = new WebScraper(connectionSettings);
        document = Jsoup.connect(correctUrl).get();
    }

    @Test
    public void testGetConnectionResponse_Success() {

        String content = "This is the content";
        Set<String> urls = Set.of("http://example.com/page1", "http://example.com/page2");

        ConnectionResponse response = webScraper.getConnectionResponse(correctUrl);

        assertNotEquals(document.body().text(), "");
        assertEquals(correctUrl, response.getPath());
        assertEquals(HttpStatus.OK.value(), response.getResponseCode());
        assertNotEquals(Collections.EMPTY_SET, response.getUrls());
        assertEquals("", response.getErrorMessage());
    }

    @Test
    public void testGetConnectionResponse_IOException() {
        String incorrectUrl = "http://qwezxcasdf.com";
        ConnectionResponse response = webScraper.getConnectionResponse(incorrectUrl);

        assertEquals(incorrectUrl, response.getPath());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getResponseCode());
        assertEquals("", response.getContent());
        assertEquals(new HashSet<>(), response.getUrls());
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), response.getErrorMessage());
    }
    @Test
    public void testGetTitle() {
        String expectedTitle = "Example Domain";
        String actualTitleWithIOException = executeGetTitle();
        String actualTitle = webScraper.getTitle(correctUrl);

        assertNotEquals("", actualTitle);
        assertNotEquals(expectedTitle, actualTitleWithIOException);
        assertEquals(expectedTitle, actualTitle);
    }
    private String executeGetTitle(){
        String result;
        try {
            webScraper.getTitle(correctUrl);
            throw new IOException("");
        }catch (IOException ioException){
            return "";
        }

    }
}
