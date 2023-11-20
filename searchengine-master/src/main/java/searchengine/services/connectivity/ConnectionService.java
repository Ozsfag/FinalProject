package searchengine.services.connectivity;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Connection2Site;

import java.io.IOException;

@Service
public class ConnectionService {
    @Autowired
    Connection2Site connection2Site;

    public ConnectionResponse getConnection(String url) {
        try {
            Connection connection = Jsoup.connect(url)
                    .userAgent(connection2Site.getUserAgent())
                    .referrer(connection2Site.getReferrer());

            Document document = connection.get();
            return buildConnectionResponse(connection, document);

        } catch (HttpStatusException e) {
            return buildErrorConnectionResponse(url, e.getStatusCode(), e.getLocalizedMessage());
        } catch (IOException e) {
            //connection object might be null, you should handle this situation.
            return buildErrorConnectionResponse(url, -1, e.getLocalizedMessage());
        }
    }

    private ConnectionResponse buildConnectionResponse(Connection connection, Document document) {
        String content = document.select("html").text();
        Elements urls = document.select("a[href]");
        int responseCode = connection.response().statusCode();
        return new ConnectionResponse(connection.request().url().toString(), responseCode, content, urls, null);
    }

    private ConnectionResponse buildErrorConnectionResponse(String url, int statusCode, String errorMessage) {
        return new ConnectionResponse(url, statusCode, null, null, errorMessage);
    }
}
