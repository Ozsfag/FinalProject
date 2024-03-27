package searchengine.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "`indexes`", schema = "search_engine",
        indexes = {
        @Index(name = "findByLemma_idAndPage_id_index", columnList = "lemma_id, page_id", unique = true)})
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class IndexModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "index_id",columnDefinition = "INT")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    private PageModel page;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lemma_id", nullable = false)
    private LemmaModel lemma;

    @Column(name = "`rank`", nullable = false, columnDefinition = "FLOAT")
    private Float rank;

    @Version
    @Column(name = "version")
    private Integer version;

}