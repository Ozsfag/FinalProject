package searchengine.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sites", schema = "search_engine",
        indexes = {
        @Index(name = "site_name_index", columnList = "name", unique = true),
        @Index(name = "site_url_index", columnList = "url", unique = true),
        @Index(name = "status_time_index", columnList = "status_time")})
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
    @Column(nullable = false)
    private Status status;

    @Column(name = "status_time", nullable = false)
    private Date statusTime;

    @Column(name = "last_error")
    private String lastError;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<PageModel> pages;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<LemmaModel> lemma;
}
