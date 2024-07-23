package searchengine.utils.connectivity;


import org.jsoup.Jsoup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import searchengine.config.ConnectionSettings;
import searchengine.dto.indexing.responseImpl.ConnectionResponse;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConnectionTest {

    private Connection connection;
    org.jsoup.Connection connect;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ConnectionSettings connectionSettings = new ConnectionSettings();
        connection = new Connection();
        connectionSettings.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");
        connectionSettings.setReferrer("https://www.google.com");
        connection.connectionSettings = connectionSettings;
        String url = "https://sendel.ru/";
        connect = Jsoup.connect(url)
                .userAgent(connectionSettings.getUserAgent())
                .referrer(connectionSettings.getReferrer())
                .ignoreHttpErrors(true);
    }

    @Test
    @DisplayName("testGetTitle")
    public void testGetConnectionResponse() {
        String content = "";
        Set<String> expectedUrls = new HashSet<>();
        String url = "https://sendel.ru/";

        ConnectionResponse response = connection.getConnectionResponse(url);

        assertEquals(url, response.getPath());
        assertEquals(HttpStatus.OK.value(), response.getResponseCode());
        assertNotEquals(content, response.getContent());
        assertNotEquals(expectedUrls, response.getUrls());
        assertEquals("", response.getErrorMessage());
    }

    @Test
    @DisplayName("testGetTitle")
    public void testGetTitle() {
        // Arrange
        String url = "https://sendel.ru/";
        String title = "Konstantin Shibkov";
        String result = connection.getTitle(url);
        assertEquals(title, result);
    }

    @Test
    @DisplayName("testGetTitleIOException")
    public void testGetTitleIOException() {
        String url = "https://sendel.ru/";
        String result = "";
        when(connection.getTitle(url)).thenThrow(new IOException());
        result = connection.getTitle(url);
        assertTrue(result.isEmpty());
        verify(connection, times(1)).getTitle(url);
    }
}
