package searchengine.web.model;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class StoppingResponse {
  boolean successfulStopping;
  String message;
}
