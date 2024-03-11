package searchengine.utils.morphology;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Component;
import searchengine.config.MorphologySettings;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class Morphology {
    private final RussianLuceneMorphology russianLuceneMorphology;
    private final EnglishLuceneMorphology englishLuceneMorphology;
    private final MorphologySettings morphologySettings;

    public Map<String, Integer> wordCounter(String content) {
        Map<String, Integer> russianCounter = wordFrequency(content, morphologySettings.getNotCyrillicLetters(), russianLuceneMorphology, morphologySettings.getRussianParticleNames());
        Map<String, Integer> englishCounter = wordFrequency(content, morphologySettings.getNotLatinLetters(), englishLuceneMorphology, morphologySettings.getEnglishParticlesNames());
        return Stream.concat(russianCounter.entrySet().parallelStream(), englishCounter.entrySet().parallelStream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<String, Integer> wordFrequency(String content, String regex, LuceneMorphology luceneMorphology, String[] particles){
        return Arrays.stream(content.toLowerCase().replaceAll(regex, morphologySettings.getEmptyString()).split(morphologySettings.getSplitter()))
                .filter(word -> isNotParticle(word, luceneMorphology, particles))
                .collect(Collectors.toMap(word -> word, word -> 1, Integer::sum));
    }

    private boolean isNotParticle(String word, LuceneMorphology luceneMorphology, String[] particles) {
        return word.length() > 2 && !word.isBlank() && Arrays.stream(particles)
                .noneMatch(part -> luceneMorphology.getMorphInfo(word).contains(part));
    }

    public Set<String> getLemmaSet(String query) {
        String onlyLatinLetters = "[a-z]+";
        Stream<String> russianLemmaStream = getLemmaStreamByLanguage(query, morphologySettings.getNotCyrillicLetters(), russianLuceneMorphology,englishLuceneMorphology, morphologySettings.getRussianParticleNames(), onlyLatinLetters);
        String onlyCyrillicLetters = "[а-я]+";
        Stream<String> englishLemmaStream = getLemmaStreamByLanguage(query, morphologySettings.getNotLatinLetters(), englishLuceneMorphology, russianLuceneMorphology, morphologySettings.getEnglishParticlesNames(), onlyCyrillicLetters);
        return Stream.concat(russianLemmaStream, englishLemmaStream).collect(Collectors.toSet());

    }
    private Stream<String> getLemmaStreamByLanguage(String query, String nonLetters, LuceneMorphology luceneMorphology1, LuceneMorphology luceneMorphology2, String[] particles, String onlyLetters){
        return Arrays.stream(query.toLowerCase().replaceAll(nonLetters, morphologySettings.getEmptyString()).split(morphologySettings.getSplitter()))
                .filter(word -> isNotParticle(word, luceneMorphology1, particles))
                .flatMap(queryWord -> queryWord.matches(onlyLetters)?
                        luceneMorphology2.getNormalForms(queryWord).stream():
                        luceneMorphology1.getNormalForms(queryWord).stream());
    }
}



