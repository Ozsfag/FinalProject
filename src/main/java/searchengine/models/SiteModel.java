package searchengine.models;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.persistence.*;
import lombok.*;

@SuppressFBWarnings("EI_EXPOSE_REP")
@Entity
@Table(
    name = "sites",
    schema = "search_engine",
    indexes = {
      @Index(name = "site_name_index", columnList = "name", unique = true),
      @Index(name = "site_url_index", columnList = "url", unique = true)
    })
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class SiteModel implements Serializable, EntityInterface {
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
  @EqualsAndHashCode.Exclude
  private Set<PageModel> pages;

  @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Set<LemmaModel> lemma;
}
