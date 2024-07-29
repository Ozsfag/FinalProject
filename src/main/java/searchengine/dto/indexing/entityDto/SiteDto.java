package searchengine.dto.indexing.entityDto;

import lombok.*;
import searchengine.model.Status;

import java.util.Date;
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class SiteDto {
    private Integer id;
    private String url;
    private String name;
    private Status status;
    private Date statusTime;
    private String lastError;
    private Integer version;
}
