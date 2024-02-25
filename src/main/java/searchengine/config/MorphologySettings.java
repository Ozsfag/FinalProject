package searchengine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "morphology-settings")
public class MorphologySettings {
    private String[] russianParticleNames;
    private String[] englishParticlesNames;
    private String notCyrillicLetters;
    private String notLatinLetters;
    private String splitter;
    private String emptyString;
    private String[] formats;
}
