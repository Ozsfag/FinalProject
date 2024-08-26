package searchengine.utils.morphology.queryHandler;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import searchengine.utils.validator.Validator;

import java.util.Arrays;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class QueryHandlerImpl implements QueryHandler {
    private final String nonLetters;
    private final LuceneMorphology luceneMorphology1;
    private final LuceneMorphology luceneMorphology2;
    private final String[] particles;
    private final String onlyLetters;
    private final Validator validator;


    public Stream<String> getLemmasFromQuery(String query) {
        return getLoweredReplacedAndSplittedQuery(query)
                .parallel()
                .filter(word -> validator.wordIsNotParticle(word, luceneMorphology1, particles))
                .flatMap(this::getInfinitivesByLanguage);
    }

    @Override
    public Stream<String> getLoweredReplacedAndSplittedQuery(String query) {
        return Arrays.stream(
                query
                        .toLowerCase()
                        .replaceAll(nonLetters, morphologySettings.getEmptyString())
                        .split(morphologySettings.getSplitter()));
    }




    @Override
    public Stream<String> getInfinitivesByLanguage(String queryWord) {
        return queryWord.matches(onlyLetters)
                ? luceneMorphology2.getNormalForms(queryWord).stream()
                : luceneMorphology1.getNormalForms(queryWord).stream();
    }
}
