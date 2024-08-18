package searchengine.dto.indexing;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import searchengine.model.Status;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteDto {
  private Integer id;
  private Status status;
  private Date statusTime;
  private String lastError;
  private String url;
  private String name;
  private Integer version;
}
