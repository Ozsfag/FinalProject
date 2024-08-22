package searchengine.dto.indexing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import searchengine.model.SiteModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LemmaDto {
  private Integer id;
  private SiteModel site;
  private String lemma;
  private Integer frequency;
  private Integer version;
}
