package searchengine.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "index_model")
@NoArgsConstructor
public class IndexModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "index_id", nullable = false)
    private Integer id;

//    @Column(name = "rank")
//    private Float rank;

    @OneToOne
    @JoinColumn(name = "page_id")
    private PageModel page;

    @OneToOne
    @JoinColumn(name = "lemma_id")
    private LemmaModel lemma;

}