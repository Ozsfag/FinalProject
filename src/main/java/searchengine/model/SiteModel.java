package searchengine.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sites", schema = "search_engine",
        indexes = {
        @Index(name = "site_name_index", columnList = "name"),
        @Index(name = "site_url_index", columnList = "url")})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SiteModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "site_id", columnDefinition = "INT")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('INDEXING', 'INDEXED', 'FAILED')")
    private Status status;

    @Column(name = "status_time", nullable = false, columnDefinition = "DATETIME")
    private Date statusTime;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String url;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String name;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<PageModel> pages;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<LemmaModel> lemma;
}
