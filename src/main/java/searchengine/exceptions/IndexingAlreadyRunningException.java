package searchengine.exceptions;

/**
 * Exception thrown when an attempt is made to start an indexing operation while another indexing
 * operation is already running.
 *
 * <p>This exception is used to prevent concurrent indexing operations that could lead to data
 * inconsistency or performance issues.
 */
public class IndexingAlreadyRunningException extends RuntimeException {
  /**
   * Constructs a new IndexingAlreadyRunningException with the specified detail message.
   *
   * @param message the detail message, which provides more information about the reason for the
   *     exception.
   */
  public IndexingAlreadyRunningException(String message) {
    super(message);
  }
}
