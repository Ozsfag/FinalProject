package searchengine.dto.indexing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import searchengine.model.SiteModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDto {
    private Integer id;
    private SiteModel site;
    private String path;
    private int code;
    private String content;
    private Integer version;

}
