package searchengine.services.morphology;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Morphology {

    private Map<String, Integer> wordCounter(String sentence){
        List<String> words = Arrays.asList(sentence.toLowerCase().replaceAll("[^а-я ]", "").split(" "));
        Map<String, Integer> wordsByFrequency = new HashMap<>();
        words.stream()
                .filter(word -> word.length() > 2 || !word.isBlank())
//                .map(word -> {
//                    RlmComponent.getInstance().getNormalForms(word).stream().findAny().equals(word)
//                })
                .forEach(word -> {
                    wordsByFrequency.putIfAbsent(word, wordsByFrequency.getOrDefault(word, 0) + 1);
                });
        return wordsByFrequency;
    }
}
