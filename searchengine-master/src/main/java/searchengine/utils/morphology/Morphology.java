package searchengine.utils.morphology;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Component;
import searchengine.utils.entityHandler.EntityHandler;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class Morphology {
    private final RussianLuceneMorphology russianLuceneMorphology;
    private final EnglishLuceneMorphology englishLuceneMorphology;
    private final EntityHandler entityHandler;
    private final String[] russianParticleNAmes = {"ÌÅÆÄ", "ÏÐÅÄË", "ÑÎÞÇ"};
    private final String[] englishParticlesNames = {"CONJ", "PREP", "ARTICLE", "INT", "PART"};
    private final String notCyrillicLetters = "[^à-ÿ]";
    private final String notLatinLetters = "[^a-z]";
    private final String onlyCyrillicLetters = "[à-ÿ]+";
    private final String onlyLatinLetters = "[a-z]+";
    private final String splitter = "\\s+";
    private final String emptyString = " ";

    public Map<String, Integer> wordCounter(String content) {
        Map<String, Integer> russianCounter = wordFrequency(content, notCyrillicLetters, russianLuceneMorphology, russianParticleNAmes);
        Map<String, Integer> englishCounter = wordFrequency(content, notLatinLetters, englishLuceneMorphology, englishParticlesNames);
        return Stream.concat(russianCounter.entrySet().stream(), englishCounter.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<String, Integer> wordFrequency(String content, String regex, LuceneMorphology luceneMorphology, String[] particles){
        return Arrays.stream(content.toLowerCase().replaceAll(regex, emptyString).split(splitter))
                .filter(word -> isNotParticle(word, luceneMorphology, particles))
                .map(luceneMorphology::getNormalForms)
                .map(forms -> forms.get(0))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(word -> word, word -> 1, Integer::sum));
    }

    private boolean isNotParticle(String word, LuceneMorphology luceneMorphology, String[] particles) {
        return word.length() > 2 && !word.isBlank() && Arrays.stream(particles)
                .noneMatch(part -> luceneMorphology.getMorphInfo(word).contains(part));
    }

    public Set<String> getLemmaSet(String query) {
    Set<String> uniqueLemma = new HashSet<>();
        Arrays.stream(query.toLowerCase(Locale.ROOT).replaceAll(notCyrillicLetters, emptyString).split(splitter))
                .filter(word -> isNotParticle(word, russianLuceneMorphology, russianParticleNAmes))
                .forEach(queryWord -> {
                    if (queryWord.matches(onlyLatinLetters)) uniqueLemma.addAll(englishLuceneMorphology.getNormalForms(queryWord));
                    uniqueLemma.addAll(russianLuceneMorphology.getNormalForms(queryWord));
                });
        Arrays.stream(query.toLowerCase().replaceAll(notLatinLetters, emptyString).split(splitter))
                .filter(word -> isNotParticle(word, englishLuceneMorphology, englishParticlesNames))
                .forEach(queryWord -> {
                    if (queryWord.matches(onlyCyrillicLetters))  uniqueLemma.addAll(russianLuceneMorphology.getNormalForms(queryWord));
                    uniqueLemma.addAll(englishLuceneMorphology.getNormalForms(queryWord));

                });
        return uniqueLemma;
    }
}



