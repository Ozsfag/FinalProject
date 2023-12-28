package searchengine.utils.morphology;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.utils.entityHandler.EntityHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Morphology {
    private final RussianLuceneMorphology luceneMorphology;
    @Lazy
    public final EntityHandler entityHandler;

    private static final String[] particlesNames = {"МЕЖД", "ПРЕДЛ", "СОЮЗ"};

    public Map<String, Integer> wordCounter(String content) {
        return Arrays.stream(content.toLowerCase().replaceAll("[^а-я]", " ").split("\\s+"))
                .filter(this::isNotParticle)
                .map(luceneMorphology::getNormalForms)
                .map(forms -> forms.get(0))
                .collect(Collectors.toMap(word -> word, word -> 1, Integer::sum));
    }


    private boolean isNotParticle(String word) {
        return word.length() > 2 && !word.isBlank() && Arrays.stream(particlesNames)
                .noneMatch(part -> luceneMorphology.getMorphInfo(word).contains(part));
    }

    public Set<String> getLemmaSet(String query) {
        Set<String> uniqueLemma = new HashSet<>();
        Arrays.stream(query.toLowerCase().replaceAll("([^а-я\\s])", " ").split("\\s+"))
                .filter(this::isNotParticle)
                .forEach(queryWord -> uniqueLemma.addAll(luceneMorphology.getNormalForms(queryWord)));
        return uniqueLemma;
    }
}



