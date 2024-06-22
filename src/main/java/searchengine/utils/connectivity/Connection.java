package searchengine.utils.connectivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import searchengine.config.ConnectionSettings;
import searchengine.dto.indexing.responseImpl.ConnectionResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * a util that parses a page
 * @author Ozsfag
 */
@Component
public class Connection {
    @Autowired
    ConnectionSettings connectionSettings;

    /**
     * get connection to url
     * @param url, link that needs to be parsed
     * @return ConnectionResponse
     */
    public ConnectionResponse getConnectionResponse(String url) {
        try {

            org.jsoup.Connection connection = Jsoup.connect(url)
                    .userAgent(connectionSettings.getUserAgent())
                    .referrer(connectionSettings.getReferrer())
                    .ignoreHttpErrors(true);

            Document document = connection.get();
            String content = Optional.of(document.body().text()).orElseThrow();
            Set<String> urls = document.select("a[href]").stream()
                    .map(element -> element.absUrl("href"))
                    .collect(Collectors.toCollection(HashSet::new));

            return new ConnectionResponse(url, HttpStatus.OK.value(), content, urls, "");
        } catch (IOException e) {
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
}
