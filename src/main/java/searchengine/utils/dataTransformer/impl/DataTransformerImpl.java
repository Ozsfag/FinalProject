package searchengine.utils.dataTransformer.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.config.SitesList;
import searchengine.dto.indexing.Site;
import searchengine.model.SiteModel;
import searchengine.utils.dataTransformer.DataTransformer;
import searchengine.utils.entityHandlers.SiteHandler;

@Component
@Lazy
public class DataTransformerImpl implements DataTransformer {
    private final SitesList sitesList;
    private final SiteHandler siteHandler;

    public DataTransformerImpl(SitesList sitesList, SiteHandler siteHandler) {
        this.sitesList = sitesList;
        this.siteHandler = siteHandler;
    }

    @Override
    public Collection<String> transformUrlToUrls(String url) {
        return Collections.singletonList(url);
    }

    @Override
    public SiteModel transformUrlToSiteModel(String url) {
        return siteHandler
                .getIndexedSiteModelFromSites(transformUrlToSites(url))
                .iterator()
                .next();
    }

    @Override
    public Collection<Site> transformUrlToSites(String url) {
        return transformUrlToUrls(url).stream()
                .map(href -> sitesList.getSites().stream()
                        .filter(siteUrl -> siteUrl.getUrl().equals(url))
                        .findFirst()
                        .orElseGet(() -> {
                            try {
                                return new Site(href, getValidUrlComponents(href)[2]);
                            } catch (URISyntaxException e) {
                                throw new RuntimeException(e);
                            }
                        }))
                .toList();
    }

    /**
     * split transmitted link into scheme and host, and path
     *
     * @param url@return valid url components
     */
    @Override
    public String[] getValidUrlComponents(String url) throws URISyntaxException {
        final URI uri = new URI(url);
        if (uri.getScheme() == null || uri.getHost() == null || uri.getPath() == null) {
            throw new URISyntaxException(url, "Invalid URL");
        }
        final String schemeAndHost = uri.getScheme() + "://" + uri.getHost() + "/";
        final String path = uri.getPath();
        final String host = uri.getHost().substring(0, uri.getHost().indexOf("."));
        return new String[] {schemeAndHost, path, host};
    }
}
