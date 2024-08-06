package searchengine.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

@Entity
@Table(
    name = "lemmas",
    schema = "search_engine",
    indexes = {@Index(name = "idx_lemma_lemma_site_id", columnList = "lemma, site_id")})
@RequiredArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class LemmaModel implements Serializable, EntityInterface {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "lemma_id", columnDefinition = "INT")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "site_id", referencedColumnName = "site_id", nullable = false)
  @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
  @ToString.Exclude
  private SiteModel site;

  @Column(name = "lemma", nullable = false, columnDefinition = "VARCHAR(255)")
  private String lemma;

  @Column(name = "frequency", nullable = false, columnDefinition = "INT")
  private Integer frequency;

  @OneToMany(mappedBy = "lemma", cascade = CascadeType.ALL)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<IndexModel> indexModels;

  @Version
  @Column(name = "version")
  private Integer version;
}
