package searchengine.utils.scraper;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import searchengine.config.ConnectionSettings;
import searchengine.config.MorphologySettings;
import searchengine.dto.indexing.responseImpl.ConnectionResponse;
import searchengine.model.SiteModel;
import searchengine.repositories.PageRepository;
import searchengine.utils.validator.Validator;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * a util that parses a page
 * @author Ozsfag
 */
@Component
@Data
@RequiredArgsConstructor
public class WebScraper {
    private final ConnectionSettings connectionSettings;
    private final PageRepository pageRepository;
    private final MorphologySettings morphologySettings;
    private final Validator validator;
    /**
     * Retrieves a Jsoup Document object for the given URL by establishing a connection with it.
     *
     * @param  url   the URL to connect to
     * @return       a Jsoup Document object representing the HTML content of the URL, or null if an IOException occurs
     */
    public Document getDocument(String url) {
        try {
            return Jsoup.connect(url)
                    .userAgent(connectionSettings.getUserAgent())
                    .referrer(connectionSettings.getReferrer())
                    .ignoreHttpErrors(true)
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e.getCause());
        }
    }
    /**
     * Retrieves the connection response for the specified URL.
     *
     * @param  url   the URL to establish a connection with
     * @return       the ConnectionResponse containing URL, HTTP status, content, URLs, and an empty string
     */
    public ConnectionResponse getConnectionResponse(String url) {
        try {
            Document document = getDocument(url);
            String content = Optional.of(document.body().text()).orElseThrow();
            Set<String> urls = document.select("a[href]").stream()
                    .map(element -> element.absUrl("href"))
                    .collect(Collectors.toSet());

            return new ConnectionResponse(url, HttpStatus.OK.value(), content, urls, "");
        } catch (Exception e) {
            return new ConnectionResponse(url, HttpStatus.NOT_FOUND.value(),"", new HashSet<>(), HttpStatus.NOT_FOUND.getReasonPhrase());
        }
    }

    /**
     *
     * @param url, link that needs to find title
     * @return title of page
     */
    public String getTitle(String url){
        org.jsoup.Connection connection = Jsoup.connect(url)
                .userAgent(connectionSettings.getUserAgent())
                .referrer(connectionSettings.getReferrer());

        Document document;
        try {
            document = connection.get();
        } catch (IOException e) {
            return "";
        }
        return document.select("title").text();
    }

    /**
     * Retrieves a set of URLs to parse based on the provided list of all URLs by site.
     *
     * @return a set of URLs to parse
     */
    public Collection<String> getUrlsToParse(SiteModel siteModel, String href) {
        Collection<String> urls = getConnectionResponse(href).getUrls();
        Collection<String> alreadyParsed = pageRepository.findAllPathsBySiteAndPathIn(siteModel.getId(), urls);
        urls.removeAll(alreadyParsed);

        return urls.parallelStream()
                .filter(url -> validator.urlHasCorrectForm(url, siteModel))
                .collect(Collectors.toSet());
    }

}
