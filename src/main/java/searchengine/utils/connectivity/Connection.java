package searchengine.utils.connectivity;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import searchengine.config.ConnectionSettings;
import searchengine.dto.indexing.responseImpl.ConnectionResponse;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * a util that parses a page
 * @author Ozsfag
 */
@Component
@RequiredArgsConstructor
public class Connection {
    final ConnectionSettings connectionSettings;

    public Document getDocument(String url) {
        try {
            return Jsoup.connect(url)
                    .userAgent(connectionSettings.getUserAgent())
                    .referrer(connectionSettings.getReferrer())
                    .ignoreHttpErrors(true)
                    .get();
        } catch (IOException e) {
            return null;
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
}
