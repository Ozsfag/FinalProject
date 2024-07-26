package searchengine.model;

import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Entity
@Table(name = "`indexes`", schema = "search_engine",
        indexes = {
            @Index(name = "idx_lemma_id_page_id", columnList = "lemma_id, page_id"),
            @Index(name = "idx_rank_lemma_id_page_id", columnList = "rank,lemma_id, page_id")})
@AllArgsConstructor
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
public class IndexModel  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "index_id",columnDefinition = "INT")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "page_id", referencedColumnName = "page_id" ,nullable = false)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ToString.Exclude
    private PageModel page;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lemma_id", referencedColumnName = "lemma_id",nullable = false)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ToString.Exclude
    private LemmaModel lemma;

    @Column(name = "`rank`", nullable = false, columnDefinition = "FLOAT")
    private Float rank;

    @Version
    @Column(name = "version")
    private Integer version;
}