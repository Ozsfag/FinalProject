package searchengine.services.morphology;

import org.apache.lucene.morphology.LuceneMorphology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.services.entityCreation.EntityCreationService;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LemmaFinder {
    @Autowired
    LemmaRepository lemmaRepository;
    @Autowired
    LuceneMorphology luceneMorphology;
    @Autowired
    IndexRepository indexRepository;

    @Autowired
    EntityCreationService entityCreationService;

    private static final String[] particlesNames = {"МЕЖД", "ПРЕДЛ", "СОЮЗ"};

    private Map<String, Integer> wordCounter(String content) {
        return Arrays.stream(content.toLowerCase().replaceAll("[^а-я]", " ").split("\\s+"))
                .filter(this::isNotParticle)
                .map(luceneMorphology::getNormalForms)
                .filter(forms -> !forms.isEmpty())
                .map(forms -> forms.get(0))
                .collect(Collectors.toMap(word -> word, word -> 1, Integer::sum));
//
    }


    private boolean isNotParticle(String word) {
        return word.length() > 2 && !word.isBlank() && Arrays.stream(particlesNames)
                .noneMatch(part -> luceneMorphology.getMorphInfo(word).contains(part));
    }

    public void handleLemmaModel( SiteModel siteModel, PageModel pageModel){
        wordCounter(pageModel.getContent()).forEach((word, frequency) -> {
            LemmaModel lemmaModel = lemmaRepository.findByLemma(word);

            if (lemmaModel == null) {
                lemmaModel = entityCreationService.createLemmaModel(siteModel, word, frequency);
            } else {
                lemmaModel.setFrequency(lemmaModel.getFrequency() + frequency);
            }

            lemmaRepository.saveAndFlush(lemmaModel);
            indexRepository.saveAndFlush(entityCreationService.createIndexModel(pageModel, lemmaModel, frequency.floatValue()));
        });
    }

}