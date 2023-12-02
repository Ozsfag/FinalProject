package searchengine.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "lemma", schema = "search_engine",
        indexes = @Index(name = "lemma_index", columnList = "lemma", unique = true))
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@ToString
public class LemmaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lemma_id", columnDefinition = "INT")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "site_id", nullable = false)
    private SiteModel site;

    @Column(name = "lemma", nullable = false, columnDefinition = "VARCHAR(255)")
    private String lemma;

    @Column(name = "frequency", nullable = false, columnDefinition = "INT")
    private  Integer frequency;

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }
}
