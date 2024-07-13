package searchengine.config;

import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MorphologyConfiguration {

    /**
     * Creates and returns a new instance of the RussianLuceneMorphology class.
     *
     * @return         	a new instance of RussianLuceneMorphology
     * @throws IOException	if an I/O error occurs
     */
    @Bean
    public RussianLuceneMorphology russianLuceneMorphology() throws IOException {
        return new RussianLuceneMorphology();
    }

    /**
     * Creates and returns a new instance of the EnglishLuceneMorphology class.
     *
     * @return         	a new instance of EnglishLuceneMorphology
     * @throws IOException	if an I/O error occurs
     */
    @Bean
    public EnglishLuceneMorphology luceneMorphology () throws IOException{
        return new EnglishLuceneMorphology();
    }
}
