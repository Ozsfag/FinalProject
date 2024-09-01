package searchengine.services.statistics.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.dto.statistics.responseImpl.StatisticsResponse;
import searchengine.model.SiteModel;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.indexing.impl.IndexingImpl;
import searchengine.services.statistics.StatisticsService;

@Service
@Lazy
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
  private final PageRepository pageRepository;
  private final LemmaRepository lemmaRepository;
  private final SiteRepository siteRepository;
  private final SitesList sites;

  /**
   * Retrieves statistics for sites, pages, and lemmas.
   *
   * @return Statistics response containing total and detailed statistics
   */
  @Override
  public StatisticsResponse getStatistics() {

    TotalStatistics total = new TotalStatistics();
    total.setSites(sites.getSites().size());
    total.setIndexing(IndexingImpl.isIndexing);
    total.setPages((int) pageRepository.count());
    total.setLemmas((int) lemmaRepository.count());

    List<DetailedStatisticsItem> detailed = new ArrayList<>();

    sites
        .getSites()
        .forEach(
            site -> {
              DetailedStatisticsItem item = new DetailedStatisticsItem();
              SiteModel siteModel = siteRepository.findSiteByUrl(site.getUrl());
              if (siteModel == null) {
                item.setName("");
                item.setUrl("");
                item.setPages(0);
                item.setLemmas(0);
                item.setStatus("");
                item.setError("");
                item.setStatusTime(new Date().getTime());
              } else {
                item.setName(site.getName());
                item.setUrl(site.getUrl());
                item.setPages(siteModel.getPages().size());
                item.setLemmas(lemmaRepository.countBySite_Url(site.getUrl()));
                item.setStatus(String.valueOf(siteModel.getStatus()));
                item.setError(siteModel.getLastError());
                item.setStatusTime(siteModel.getStatusTime().getTime());
              }
              detailed.add(item);
            });

    StatisticsData data = new StatisticsData();
    data.setTotal(total);
    data.setDetailed(detailed);

    return new StatisticsResponse(true, data);
  }
}
