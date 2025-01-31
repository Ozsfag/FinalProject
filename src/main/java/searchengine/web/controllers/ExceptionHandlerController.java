package searchengine.web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import searchengine.exceptions.IndexingAlreadyRunningException;
import searchengine.exceptions.StoppedExecutionException;
import searchengine.web.models.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerController {
  @ExceptionHandler(IndexingAlreadyRunningException.class)
  public ResponseEntity<ErrorResponse> indexingAlreadyRunning(IndexingAlreadyRunningException ex) {
    log.error("Индексация уже запущена", ex);
    return ResponseEntity.status(HttpStatus.IM_USED)
        .body(new ErrorResponse(false, ex.getLocalizedMessage()));
  }

  @ExceptionHandler(StoppedExecutionException.class)
  public ResponseEntity<ErrorResponse> stoppedExecution(StoppedExecutionException ex) {
    log.error("Индексация остановлена пользователем", ex);
    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
        .body(new ErrorResponse(false, ex.getLocalizedMessage()));
  }

  @ExceptionHandler(BindException.class)
  public ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
    String errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .findFirst()
            .get();
    String errorMessage = String.join("; ", errors);
    log.error("Validation error: {}", errorMessage);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(false, errorMessage));
  }
}
