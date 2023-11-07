package searchengine.services.morphology;

import org.springframework.stereotype.Service;
import searchengine.services.components.RlmComponent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Morphology {

    private Map<String, Integer> wordCounter(String sentence){
        List<String> words = Arrays.asList(sentence.toLowerCase().replaceAll("[^а-я ]", "").split(" "));
        Map<String, Integer> wordsByFrequency = new HashMap<>();
        List<String> wordBaseForms; RlmComponent.getInstance().getMorphInfo("или");
        words.stream()
                .filter(word -> word.length() > 2 || !word.isBlank())
                .forEach(word -> {
//                    wordsByFrequency.computeIfAbsent(word, w-> wordsByFrequency.);
                });
        return wordsByFrequency;
    }
}
