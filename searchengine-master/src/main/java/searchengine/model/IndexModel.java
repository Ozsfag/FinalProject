package searchengine.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "indexModel", schema = "search_engine")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class IndexModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "index_id", nullable = false)
    private Integer id;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "page_id")
    private PageModel page;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "lemma_id")
    private LemmaModel lemma;

//    @Column(name = "rank", columnDefinition = "FLOAT", nullable = false)
//    private final  Float rank;

}