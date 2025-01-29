package searchengine.web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import searchengine.exceptions.IndexingAlreadyRunningException;
import searchengine.exceptions.NotInConfigurationException;
import searchengine.exceptions.StoppedExecutionException;
import searchengine.web.model.ErrorResponse;

import java.net.URISyntaxException;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerController {
    @ExceptionHandler(IndexingAlreadyRunningException.class)
    public ResponseEntity<ErrorResponse> indexingAlreadyRunning(IndexingAlreadyRunningException ex) {
        log.error("Индексация уже запущена", ex);
        return ResponseEntity
                .status(HttpStatus.IM_USED)
                .body(new ErrorResponse(false, ex.getLocalizedMessage()));
    }

    @ExceptionHandler(StoppedExecutionException.class)
    public ResponseEntity<ErrorResponse> stoppedExecution(StoppedExecutionException ex) {
        log.error("Индексация остановлена пользователем", ex);
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(new ErrorResponse(false, ex.getLocalizedMessage()));
    }

    @ExceptionHandler(NotInConfigurationException.class)
    public ResponseEntity<ErrorResponse> parsedPageNotContainsInConfiguration(NotInConfigurationException ex) {
        log.error("Ошибка при индексации страницы. Сраница не содержится с конфигурации.");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(false, ex.getLocalizedMessage()));
    }

    @ExceptionHandler(URISyntaxException.class)
    public ResponseEntity<ErrorResponse> wrongFormatOfParsedUrl(URISyntaxException ex) {
        log.error("Ошибка при индексации страницы. Сраница имеет неправильынй формат.");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(false, ex.getLocalizedMessage()));
    }
}
