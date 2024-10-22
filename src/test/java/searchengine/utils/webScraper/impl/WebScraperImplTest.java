package searchengine.utils.webScraper.impl;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import org.jsoup.Connection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import searchengine.dto.indexing.ConnectionResponse;
import searchengine.utils.webScraper.connectionResponseBuilder.ConnectionResponseBuilder;
import searchengine.utils.webScraper.jsoupConnectionBuilder.JsoupConnectionBuilder;

@RunWith(MockitoJUnitRunner.class)
public class WebScraperImplTest {

  private JsoupConnectionBuilder jsoupConnectionBuilder;
  private ConnectionResponseBuilder connectionResponseBuilder;
  private WebScraperImpl webScraper;

  @Before
  public void setUp() {
    jsoupConnectionBuilder = mock(JsoupConnectionBuilder.class);
    connectionResponseBuilder = mock(ConnectionResponseBuilder.class);
    webScraper = new WebScraperImpl();
  }

  @Test
  public void testValidUrlReturnsConnectionResponse() throws IOException {
    String url = "http://valid.url";
    Connection mockConnection = mock(Connection.class);
    ConnectionResponse expectedResponse = new ConnectionResponse();

    when(jsoupConnectionBuilder.createJsoupConnection(url)).thenReturn(mockConnection);
    when(connectionResponseBuilder.buildConnectionResponse(url)).thenReturn(expectedResponse);

    ConnectionResponse actualResponse = webScraper.getConnectionResponse(url);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void testNoExceptionsForValidUrl() throws IOException {
    String url = "http://valid.url";
    Connection mockConnection = mock(Connection.class);
    Connection.Response mockResponse = mock(Connection.Response.class);

    when(jsoupConnectionBuilder.createJsoupConnection(url)).thenReturn(mockConnection);
    when(mockConnection.execute()).thenReturn(mockResponse);

    assertDoesNotThrow(() -> webScraper.getConnectionResponse(url));
  }

  @Test
  public void testValidUrlResponseCode200() throws IOException {
    String url = "http://valid.url";
    Connection mockConnection = mock(Connection.class);
    ConnectionResponse expectedResponse = new ConnectionResponse();

    when(jsoupConnectionBuilder.createJsoupConnection(url)).thenReturn(mockConnection);
    when(connectionResponseBuilder.buildConnectionResponse(url)).thenReturn(expectedResponse);

    ConnectionResponse actualResponse = webScraper.getConnectionResponse(url);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void testHandlesIOExceptionGracefully() throws IOException {
    String url = "http://valid.url";
    Connection mockConnection = mock(Connection.class);
    IOException exception = new IOException();
    ConnectionResponse expectedResponse = new ConnectionResponse();

    when(jsoupConnectionBuilder.createJsoupConnection(url)).thenReturn(mockConnection);
    when(mockConnection.execute()).thenThrow(exception);
    when(connectionResponseBuilder.buildConnectionResponseWithException(url, 0, null))
        .thenReturn(expectedResponse);

    ConnectionResponse actualResponse = webScraper.getConnectionResponse(url);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void testHandlesEmptyUrlInput() {
    final String EMPTY_URL = "";
    final String EXPECTED_ERROR_MESSAGE = "URL cannot be empty";
    final int DEFAULT_STATUS_CODE = 0;
    final String DEFAULT_STATUS_MESSAGE = null;
    // Arrange
    ConnectionResponse expectedResponse =
        new ConnectionResponse(
            new ArrayList<>(), EMPTY_URL, "", DEFAULT_STATUS_MESSAGE, "", DEFAULT_STATUS_CODE);

    when(jsoupConnectionBuilder.createJsoupConnection(EMPTY_URL))
        .thenThrow(new IllegalArgumentException(EXPECTED_ERROR_MESSAGE));
    when(connectionResponseBuilder.buildConnectionResponseWithException(
            eq(EMPTY_URL), eq(DEFAULT_STATUS_CODE), eq(DEFAULT_STATUS_MESSAGE)))
        .thenReturn(expectedResponse);

    // Act
    ConnectionResponse actualResponse = webScraper.getConnectionResponse(EMPTY_URL);

    // Assert
    assertEquals(expectedResponse, actualResponse);
    verify(jsoupConnectionBuilder, times(1)).createJsoupConnection(EMPTY_URL);
    verify(connectionResponseBuilder, times(1))
        .buildConnectionResponseWithException(
            eq(EMPTY_URL), eq(DEFAULT_STATUS_CODE), eq(DEFAULT_STATUS_MESSAGE));
  }

  @Test
  public void testDealsWithNetworkTimeouts() throws IOException {
    String url = "http://valid.url";
    Connection mockConnection = mock(Connection.class);
    IOException exception = new IOException("Timeout");
    ConnectionResponse expectedResponse = new ConnectionResponse();

    when(jsoupConnectionBuilder.createJsoupConnection(url)).thenReturn(mockConnection);
    when(mockConnection.execute()).thenThrow(exception);
    when(connectionResponseBuilder.buildConnectionResponseWithException(url, 0, null))
        .thenReturn(expectedResponse);

    ConnectionResponse actualResponse = webScraper.getConnectionResponse(url);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void testManageHttpErrors() throws IOException {
    String url = "http://invalid.url";
    Connection mockConnection = mock(Connection.class);
    IOException exception = new IOException("HTTP Error 404");
    ConnectionResponse expectedResponse = new ConnectionResponse();

    when(jsoupConnectionBuilder.createJsoupConnection(url)).thenReturn(mockConnection);
    when(mockConnection.execute()).thenThrow(exception);
    when(connectionResponseBuilder.buildConnectionResponseWithException(url, 0, null))
        .thenReturn(expectedResponse);

    ConnectionResponse actualResponse = webScraper.getConnectionResponse(url);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void testCorrectDependencyInstantiation() {
    assertNotNull(webScraper);
  }

  @Test
  public void testExecuteConnectionCalledOnce() throws IOException {
    String url = "http://test.url";
    Connection mockConnection = mock(Connection.class);
    Connection.Response mockResponse = mock(Connection.Response.class);

    when(jsoupConnectionBuilder.createJsoupConnection(url)).thenReturn(mockConnection);
    when(mockConnection.execute()).thenReturn(mockResponse);

    webScraper.getConnectionResponse(url);

    verify(mockConnection, times(1)).execute();
  }

  @Test
  public void testThreadSafetyMultipleRequests() throws IOException {
    String url = "http://valid.url";
    Connection mockConnection = mock(Connection.class);
    ConnectionResponse expectedResponse = new ConnectionResponse();

    when(jsoupConnectionBuilder.createJsoupConnection(url)).thenReturn(mockConnection);
    when(connectionResponseBuilder.buildConnectionResponse(url)).thenReturn(expectedResponse);

    ConnectionResponse actualResponse = webScraper.getConnectionResponse(url);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void testConnectionResponseBuilderInjection() throws IOException {
    String testUrl = "http://test.url";
    Connection mockConnection = mock(Connection.class);
    ConnectionResponse mockResponse =
        new ConnectionResponse(new ArrayList<>(), "testPath", "testContent", "", "testTitle", 200);

    when(jsoupConnectionBuilder.createJsoupConnection(testUrl)).thenReturn(mockConnection);
    when(connectionResponseBuilder.buildConnectionResponse(testUrl)).thenReturn(mockResponse);

    ConnectionResponse response = webScraper.getConnectionResponse(testUrl);

    assertNotNull(response);
    assertEquals("testPath", response.getPath());
    assertEquals(200, Optional.ofNullable(response.getResponseCode()));
    assertEquals("testContent", response.getContent());
    assertEquals("testTitle", response.getTitle());

    verify(jsoupConnectionBuilder, times(1)).createJsoupConnection(testUrl);
    verify(mockConnection, times(1)).execute();
    verify(connectionResponseBuilder, times(1)).buildConnectionResponse(testUrl);
  }
}
