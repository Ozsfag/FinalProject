package searchengine.config;

import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RlmComponent {
    private static volatile RussianLuceneMorphology russianLuceneMorphology;

    private RlmComponent() {
    }
    public static RussianLuceneMorphology getInstance(){
        if (russianLuceneMorphology == null){
            try {
                russianLuceneMorphology = new RussianLuceneMorphology();
            } catch (IOException e) {
                e.getLocalizedMessage();
            }
        }
        return russianLuceneMorphology;
    }
}
