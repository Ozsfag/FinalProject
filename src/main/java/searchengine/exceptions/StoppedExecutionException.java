package searchengine.exceptions;

public class StoppedExecutionException extends RuntimeException{
    public StoppedExecutionException(String message) {
        super(message);
    }
}
