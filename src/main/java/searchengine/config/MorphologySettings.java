package searchengine.config;

import java.util.Collection;
import java.util.Collections;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "morphology-settings")
@NoArgsConstructor(force = true)
public class MorphologySettings {
  private Collection<String> russianParticleNames;
  private Collection<String> englishParticlesNames;
  private Collection<String> formats;
  private Collection<String> allowedSchemas;
  @Setter private String notCyrillicLetters;
  @Setter private String notLatinLetters;
  @Setter private String splitter;
  @Setter private String emptyString;
  @Setter private String onlyLatinLetters;
  @Setter private String onlyCyrillicLetters;
  @Setter @Getter private Integer maxFrequency;

  public MorphologySettings(
      Collection<String> russianParticleNames,
      Collection<String> englishParticlesNames,
      String notCyrillicLetters,
      String notLatinLetters,
      String splitter,
      String emptyString,
      Collection<String> formats,
      Collection<String> allowedSchemas,
      String onlyLatinLetters,
      String onlyCyrillicLetters,
      Integer maxFrequency) {
    this.russianParticleNames = Collections.unmodifiableCollection(russianParticleNames);
    this.englishParticlesNames = Collections.unmodifiableCollection(englishParticlesNames);
    this.notCyrillicLetters = notCyrillicLetters;
    this.notLatinLetters = notLatinLetters;
    this.splitter = splitter;
    this.emptyString = emptyString;
    this.formats = Collections.unmodifiableCollection(formats);
    this.allowedSchemas = Collections.unmodifiableCollection(allowedSchemas);
    this.onlyLatinLetters = onlyLatinLetters;
    this.onlyCyrillicLetters = onlyCyrillicLetters;
    this.maxFrequency = maxFrequency;
  }

  public void setRussianParticleNames(Collection<String> russianParticleNames) {
    this.russianParticleNames = Collections.unmodifiableCollection(russianParticleNames);
  }

  public void setEnglishParticlesNames(Collection<String> englishParticlesNames) {
    this.englishParticlesNames = Collections.unmodifiableCollection(englishParticlesNames);
  }

  public void setFormats(Collection<String> formats) {
    this.formats = Collections.unmodifiableCollection(formats);
  }

  public void setAllowedSchemas(Collection<String> allowedSchemas) {
    this.allowedSchemas = Collections.unmodifiableCollection(allowedSchemas);
  }

  public Collection<String> getRussianParticleNames() {
    return Collections.unmodifiableCollection(russianParticleNames);
  }

  public Collection<String> getEnglishParticlesNames() {
    return Collections.unmodifiableCollection(englishParticlesNames);
  }

  public String getNotCyrillicLetters() {
    return String.copyValueOf(notCyrillicLetters.toCharArray());
  }

  public String getNotLatinLetters() {
    return String.copyValueOf(notLatinLetters.toCharArray());
  }

  public String getSplitter() {
    return String.copyValueOf(splitter.toCharArray());
  }

  public String getEmptyString() {
    return String.copyValueOf(emptyString.toCharArray());
  }

  public Collection<String> getFormats() {
    return Collections.unmodifiableCollection(formats);
  }

  public Collection<String> getAllowedSchemas() {
    return Collections.unmodifiableCollection(allowedSchemas);
  }

  public String getOnlyLatinLetters() {
    return String.copyValueOf(onlyLatinLetters.toCharArray());
  }

  public String getOnlyCyrillicLetters() {
    return String.copyValueOf(onlyCyrillicLetters.toCharArray());
  }
}
