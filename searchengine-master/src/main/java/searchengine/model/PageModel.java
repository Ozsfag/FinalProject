package searchengine.model;
import lombok.*;
import javax.persistence.*;
import javax.persistence.Index;

@Entity
@Table( name = "pages", schema = "search_engine",
        indexes = @Index(name = "path_index", columnList = "path", unique = true))
@Getter
@Setter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class PageModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "page_id", columnDefinition = "INT")
    private Integer id;


    @ManyToOne
    @JoinColumn(name = "site_id")
    private final SiteModel site;


    @Column(name = "path", nullable = false, columnDefinition = "TEXT")
    private final String path;


    @Column(nullable = false, columnDefinition = "INT")
    private final int code;


    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private final String content;
}
