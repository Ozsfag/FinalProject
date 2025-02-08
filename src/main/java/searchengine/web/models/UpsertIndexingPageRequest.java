package searchengine.web.models;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import searchengine.validators.annotations.URIValidator;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpsertIndexingPageRequest {
  @NotNull(message = "URL cannot be null")
  @NotEmpty(message = "URL cannot be empty")
  @URL(message = "Invalid URL format")
  @URIValidator(message = "URL host is not in the allowed sites list")
  private String url;
}
