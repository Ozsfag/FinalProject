package searchengine.config;

import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MorphologyConfiguration {
    @Bean
    public RussianLuceneMorphology russianLuceneMorphology() throws IOException {
        return new RussianLuceneMorphology();
    }

    @Bean
    public EnglishLuceneMorphology luceneMorphology () throws IOException{
        return new EnglishLuceneMorphology();
    }
}
