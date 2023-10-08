package searchengine.services.startIndexing;

import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import searchengine.config.Connection2Site;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.model.repositories.PageRepository;
import searchengine.model.repositories.SiteRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.RecursiveAction;

@RequiredArgsConstructor
public class Parser extends RecursiveAction {

    private final SiteModel siteModel;
    private final PageRepository pageRepository;
    private final Connection2Site connection2Site;
    private final SiteRepository siteRepository;
    private int responseCode;
    private String content;
    @Override
    protected  void compute(){
        List<Parser> taskList = new ArrayList<>();
        try {
            Thread.sleep(5000);
            Connection connection =  Jsoup.connect(this.siteModel.getUrl())
                    .userAgent(connection2Site.getUserAgent())
                    .referrer(connection2Site.getReferrer());

            Document document = connection.get();
            content = document.html();
            responseCode = connection.response().statusCode();
            Elements urls = document.select("a[href]");

            urls.stream()
                    .map(item -> PageModel.builder()
                              .site(siteModel)
                              .code(responseCode)
                              .content(content)
                              .path(item.absUrl("href"))
                              .build())
                    .filter(page -> pageRepository.findByPath(page.getPath()) == null
                                    && page.getPath().startsWith(this.siteModel.getUrl()))
                    .forEach(pageModel -> {
                        pageRepository.saveAndFlush(pageModel);
                        this.siteModel.setStatusTime(new Date());
                        siteRepository.saveAndFlush(this.siteModel);
                        taskList.add(new Parser(this.siteModel, pageRepository, connection2Site, siteRepository));
                    });

        } catch (Exception e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
        taskList.forEach(RecursiveAction::fork);
        taskList.forEach(RecursiveAction::join);
    }
}