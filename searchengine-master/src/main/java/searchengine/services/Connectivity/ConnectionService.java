package searchengine.services.Connectivity;

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
        Connection connection = Jsoup.connect(url)
                .userAgent(connection2Site.getUserAgent())
                .referrer(connection2Site.getReferrer());

        Document document;
        String content;
        Elements urls;
        try {
            document = connection.get();
            content = document.html();
            urls = document.select("a[href]");
            int responseCode = connection.response().statusCode();
            return ConnectionResponse.builder()
                    .path(url)
                    .content(content)
                    .urls(urls)
                    .responseCode(responseCode)
                    .build();
        } catch (HttpStatusException e){
            return ConnectionResponse.builder()
                    .path(url)
                    .responseCode(e.getStatusCode())
                    .errorMessage(e.getLocalizedMessage())
                    .build();
        }catch (IOException e){
            return ConnectionResponse.builder()
                    .path(url)
                    .errorMessage(e.getLocalizedMessage())
                    .build();
        }
    }
}
