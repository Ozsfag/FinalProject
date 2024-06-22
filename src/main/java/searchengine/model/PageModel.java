package searchengine.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table( name = "pages", schema = "search_engine",
        indexes = @Index(name = "path_index", columnList = "path", unique = true))
@Data
@NoArgsConstructor(force = true)
@Builder
@AllArgsConstructor
@ToString
public class PageModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "page_id", columnDefinition = "INT")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    @ToString.Exclude
    private SiteModel site;


    @Column(name = "path", nullable = false, columnDefinition = "TEXT")
    private String path;


    @Column(nullable = false, columnDefinition = "INT")
    private int code;


    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<IndexModel> indexModels;
}
