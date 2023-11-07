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

    public ConnectionResponse getConnection(String url){
        Connection connection = null;
        Document document;
        String content;
        Elements urls;
        try {
            connection = Jsoup.connect(url)
                    .userAgent(connection2Site.getUserAgent())
                    .referrer(connection2Site.getReferrer());
            document = connection.get();
            content = document.select("html").text();
            urls = document.select("a[href]");
            int responseCode = connection.response().statusCode();
            return new ConnectionResponse(url, responseCode, content, urls, null);
        }catch (HttpStatusException e){
            return new ConnectionResponse(url, e.getStatusCode(), null, null, e.getLocalizedMessage());
        }catch (IOException e){
            return new ConnectionResponse(url, connection.response().statusCode(), null, null, e.getLocalizedMessage());
        }
    }
}
