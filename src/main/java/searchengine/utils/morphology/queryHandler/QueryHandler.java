package searchengine.utils.morphology.queryHandler;

import java.util.stream.Stream;

public interface QueryHandler {
  Stream<String> getLemmasFromQuery(String query);
}
