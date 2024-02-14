package searchengine.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "lemma", schema = "search_engine",
        indexes = {
        @Index(name = "lemma_index", columnList = "lemma", unique = true),
        @Index(name = "frequency_index", columnList = "frequency"),
        @Index(name = "lemma_site_index", columnList = "site_id")})
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

    @OneToMany(mappedBy = "lemma", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<IndexModel> indexModels;
}
