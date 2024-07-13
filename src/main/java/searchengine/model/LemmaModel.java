package searchengine.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "lemmas", schema = "search_engine",
        indexes = {
            @Index(name = "idx_lemma_lemma_site_id", columnList = "lemma, site_id"),
            @Index(name = "idx_lemma_lemma_site_id_frequency", columnList = "lemma, site_id, frequency")
        })
@RequiredArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class LemmaModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lemma_id", columnDefinition = "INT")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    @ToString.Exclude
    private SiteModel site;

    @Column(name = "lemma", nullable = false, columnDefinition = "VARCHAR(255)")
    private String lemma;

    @Column(name = "frequency", nullable = false, columnDefinition = "INT")
    private  Integer frequency;

    @OneToMany(mappedBy = "lemma", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<IndexModel> indexModels;

    @Version
    @Column(name = "version")
    private Integer version;
}