package searchengine.services.morphology;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.repositories.LemmaRepository;
import searchengine.config.RlmComponent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class Morphology {
    @Autowired
    LemmaRepository lemmaRepository;
    private static final String[] particlesNames = {"МЕЖД", "ПРЕДЛ", "СОЮЗ"};

    public Map<String, Integer> wordCounter(String sentence) {
        Map<String, Integer> wordsByFrequency = new HashMap<>();
        Arrays.stream(sentence.toLowerCase().replaceAll("[^а-я]", " ").split("\\s+"))
                .filter(this::isWordValid)
                .map(RlmComponent.getInstance()::getNormalForms)
                .filter(forms -> !forms.isEmpty())
                .map(forms -> forms.get(0))
                .forEach(lemmaWord -> wordsByFrequency.compute(lemmaWord, (k, v) -> wordsByFrequency.containsKey(k) ? v + 1 : 1));
        return wordsByFrequency;
    }

    boolean isWordValid(String word) {
        return word.length() > 2 && !word.isBlank() && Arrays.stream(particlesNames).noneMatch(part -> RlmComponent.getInstance().getMorphInfo(word).contains(part));
    }
}