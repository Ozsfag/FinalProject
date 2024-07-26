package searchengine.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import searchengine.dto.ResponseInterface;
import searchengine.dto.indexing.responseImpl.Successful;
import searchengine.services.deleting.DeletingService;
import searchengine.services.indexing.IndexingService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ApiControllerTest {

    @InjectMocks
    private ApiController apiController;

    @Mock
    private DeletingService deletingService;

    @Mock
    private IndexingService indexingService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testStartIndexing() {
        // Arrange
        ReflectionTestUtils.setField(apiController, "deletingService", deletingService);
        ReflectionTestUtils.setField(apiController, "indexingService", indexingService);
        when(indexingService.startIndexing()).thenReturn(new Successful(true));

        // Act
        ResponseEntity<ResponseInterface> result = apiController.startIndexing();

        // Assert
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(new Successful(true), result.getBody());
        verify(deletingService, times(1)).deleteData();
        verify(indexingService, times(1)).startIndexing();

    }
}