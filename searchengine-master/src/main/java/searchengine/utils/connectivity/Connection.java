package searchengine.utils.connectivity;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import searchengine.config.Connection2Site;
import searchengine.dto.indexing.responseImpl.ConnectionResponse;

import java.io.IOException;

@Component
public class Connection {
    @Autowired
    Connection2Site connection2Site;

    public ConnectionResponse getConnection(String url) {
        try {
            org.jsoup.Connection connection = Jsoup.connect(url)
                    .userAgent(connection2Site.getUserAgent())
                    .referrer(connection2Site.getReferrer());

            Document document = connection.get();

            int responseCode = connection.response().statusCode();
            String content = document.select("html").text();
            Elements urls = document.select("a[href]");
            return new ConnectionResponse(connection.request().url().toString(), responseCode, content, urls, null);

        } catch (HttpStatusException e) {
            return buildErrorConnectionResponse(url, e.getStatusCode(),e.getMessage());
        } catch (IOException e) {
            return buildErrorConnectionResponse(url, HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }
    public String getTitle(String url){
        try {
            org.jsoup.Connection connection = Jsoup.connect(url)
                    .userAgent(connection2Site.getUserAgent())
                    .referrer(connection2Site.getReferrer());

            Document document = connection.get();

            return document.select("title").text();

        } catch (IOException e) {
            return "";
        }
    }

    private ConnectionResponse buildErrorConnectionResponse(String url, int statusCode, String errorMessage) {
        return new ConnectionResponse(url, statusCode,"", null, errorMessage);
    }
}
