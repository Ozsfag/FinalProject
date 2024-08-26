package searchengine.utils.morphology.queryHandler;

import java.util.stream.Stream;

public interface QueryHandler {
  /**
   * Returns a stream of strings representing the lemmas extracted from the given query.
   *
   * @param query the query from which to extract lemmas
   * @return a stream of strings representing the lemmas extracted from the query
   */
  Stream<String> getLemmasFromQuery(String query);
}
