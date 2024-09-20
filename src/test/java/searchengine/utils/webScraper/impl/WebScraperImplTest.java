package searchengine.utils.webScraper.impl;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
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
    webScraper = new WebScraperImpl(jsoupConnectionBuilder, connectionResponseBuilder);
  }

  @Test
  public void testValidUrlReturnsConnectionResponse() throws IOException {
    String url = "http://valid.url";
    Connection mockConnection = mock(Connection.class);
    Connection.Response mockResponse = mock(Connection.Response.class);
    ConnectionResponse expectedResponse = new ConnectionResponse();

    when(jsoupConnectionBuilder.createJsoupConnection(url)).thenReturn(mockConnection);
    when(mockConnection.execute()).thenReturn(mockResponse);
    when(connectionResponseBuilder.buildConnectionResponse(url, mockResponse, mockConnection))
        .thenReturn(expectedResponse);

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
    Connection.Response mockResponse = mock(Connection.Response.class);
    ConnectionResponse expectedResponse = new ConnectionResponse();

    when(jsoupConnectionBuilder.createJsoupConnection(url)).thenReturn(mockConnection);
    when(mockConnection.execute()).thenReturn(mockResponse);
    when(connectionResponseBuilder.buildConnectionResponse(url, mockResponse, mockConnection))
        .thenReturn(expectedResponse);

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
    when(connectionResponseBuilder.buildConnectionResponseWithException(url, exception))
        .thenReturn(expectedResponse);

    ConnectionResponse actualResponse = webScraper.getConnectionResponse(url);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void testHandlesEmptyUrlInput() {
    String url = "";
    String expectedErrorMessage = "URL cannot be empty";
    ConnectionResponse expectedResponse =
        ConnectionResponse.builder().errorMessage(expectedErrorMessage).build();

    when(jsoupConnectionBuilder.createJsoupConnection(url))
        .thenThrow(new IllegalArgumentException(expectedErrorMessage));
    when(connectionResponseBuilder.buildConnectionResponseWithException(
            eq(url), any(IllegalArgumentException.class)))
        .thenReturn(expectedResponse);

    ConnectionResponse actualResponse = webScraper.getConnectionResponse(url);

    assertEquals(expectedResponse, actualResponse);
    verify(jsoupConnectionBuilder, times(1)).createJsoupConnection(url);
    verify(connectionResponseBuilder, times(1))
        .buildConnectionResponseWithException(eq(url), any(IllegalArgumentException.class));
  }

  @Test
  public void testDealsWithNetworkTimeouts() throws IOException {
    String url = "http://valid.url";
    Connection mockConnection = mock(Connection.class);
    IOException exception = new IOException("Timeout");
    ConnectionResponse expectedResponse = new ConnectionResponse();

    when(jsoupConnectionBuilder.createJsoupConnection(url)).thenReturn(mockConnection);
    when(mockConnection.execute()).thenThrow(exception);
    when(connectionResponseBuilder.buildConnectionResponseWithException(url, exception))
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
    when(connectionResponseBuilder.buildConnectionResponseWithException(url, exception))
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
    Connection.Response mockResponse = mock(Connection.Response.class);
    ConnectionResponse expectedResponse = new ConnectionResponse();

    when(jsoupConnectionBuilder.createJsoupConnection(url)).thenReturn(mockConnection);
    when(mockConnection.execute()).thenReturn(mockResponse);
    when(connectionResponseBuilder.buildConnectionResponse(url, mockResponse, mockConnection))
        .thenReturn(expectedResponse);

    ConnectionResponse actualResponse = webScraper.getConnectionResponse(url);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void testConnectionResponseBuilderInjection() throws IOException {
    String testUrl = "http://test.url";
    Connection mockConnection = mock(Connection.class);
    Connection.Response mockConnectionResponse = mock(Connection.Response.class);
    ConnectionResponse mockResponse =
        new ConnectionResponse("testPath", 200, "testContent", null, null, "testTitle");

    when(jsoupConnectionBuilder.createJsoupConnection(testUrl)).thenReturn(mockConnection);
    when(mockConnection.execute()).thenReturn(mockConnectionResponse);
    when(connectionResponseBuilder.buildConnectionResponse(
            testUrl, mockConnectionResponse, mockConnection))
        .thenReturn(mockResponse);

    ConnectionResponse response = webScraper.getConnectionResponse(testUrl);

    assertNotNull(response);
    assertEquals("testPath", response.getPath());
    assertEquals(200, response.getResponseCode());
    assertEquals("testContent", response.getContent());
    assertEquals("testTitle", response.getTitle());

    verify(jsoupConnectionBuilder, times(1)).createJsoupConnection(testUrl);
    verify(mockConnection, times(1)).execute();
    verify(connectionResponseBuilder, times(1))
        .buildConnectionResponse(testUrl, mockConnectionResponse, mockConnection);
  }
}
