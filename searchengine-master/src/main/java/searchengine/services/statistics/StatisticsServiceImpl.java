package searchengine.services.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.indexing.IndexingImpl;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final SiteRepository siteRepository;
    private final SitesList sites;

    @Override
    public StatisticsResponse getStatistics() {

        TotalStatistics total = new TotalStatistics();
        total.setSites(sites.getSites().size());
        total.setIndexing(IndexingImpl.isIndexing.get());
        total.setPages((int)pageRepository.count());
        total.setLemmas((int) lemmaRepository.count());

        List<DetailedStatisticsItem> detailed = new ArrayList<>();

        sites.getSites().forEach(site -> {
            DetailedStatisticsItem item = new DetailedStatisticsItem();
            item.setName(site.getName());
            item.setUrl(site.getUrl());
            item.setPages(siteRepository.findByUrl(site.getUrl()).getPages().size());
            item.setLemmas(lemmaRepository.countBySite_Url(site.getUrl()));
            item.setStatus(String.valueOf(siteRepository.findByUrl(site.getUrl()).getStatus()));
            item.setError(siteRepository.findByUrl(site.getUrl()).getLastError());
            item.setStatusTime(siteRepository.findByUrl(site.getUrl()).getStatusTime().getTime());
            detailed.add(item);
        });


        StatisticsResponse response = new StatisticsResponse();
        StatisticsData data = new StatisticsData();
        data.setTotal(total);
        data.setDetailed(detailed);
        response.setStatistics(data);
        response.setResult(true);
        return response;
    }
}
