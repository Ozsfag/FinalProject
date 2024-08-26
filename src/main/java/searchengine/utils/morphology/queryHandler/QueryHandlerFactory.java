package searchengine.utils.morphology.queryHandler;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;
import searchengine.utils.validator.Validator;

@Component
@RequiredArgsConstructor
public class QueryHandlerFactory {
    private final RussianLuceneMorphology russianLuceneMorphology;
    private final EnglishLuceneMorphology englishLuceneMorphology;
    private final MorphologySettings morphologySettings;
    private final Validator validator;

    public QueryHandler createRussianQueryHandler() {
        return new QueryHandlerImpl(
                morphologySettings.getNotCyrillicLetters(),
                russianLuceneMorphology,
                englishLuceneMorphology,
                morphologySettings.getRussianParticleNames(),
                morphologySettings.getOnlyLatinLetters(),
                validator);
    }
}
