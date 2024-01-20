package searchengine.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "index_model", schema = "search_engine")
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class IndexModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "index_id",columnDefinition = "INT")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "page_id", nullable = false)
    private PageModel page;

    @ManyToOne
    @JoinColumn(name = "lemma_id", nullable = false)
    private LemmaModel lemma;

    @Column(nullable = false)
    private Float ranking;


}