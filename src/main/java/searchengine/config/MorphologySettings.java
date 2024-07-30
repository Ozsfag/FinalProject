package searchengine.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
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
    private String[] allowedSchemas;
    private int maxFrequency;
}
