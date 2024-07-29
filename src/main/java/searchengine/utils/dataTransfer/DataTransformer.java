package searchengine.utils.dataTransfer;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import searchengine.config.SitesList;
import searchengine.dto.indexing.Site;
import searchengine.dto.indexing.entityDto.IndexDto;
import searchengine.dto.indexing.entityDto.LemmaDto;
import searchengine.dto.indexing.entityDto.PageDto;
import searchengine.dto.indexing.entityDto.SiteDto;
import searchengine.exceptions.OutOfSitesConfigurationException;
import searchengine.model.*;
import searchengine.utils.validator.Validator;

import java.util.Collection;
import java.util.Collections;

@Component
@Data
@RequiredArgsConstructor
public class DataTransformer {
    private final SitesList sitesList;
    private final Validator validator;
    public Collection<String> transformUrlToUrls(String url){
        return Collections.singletonList(url);
    }

    @SneakyThrows
    public Collection<Site> transformUrlToSites(String url) {
        return transformUrlToUrls(url).stream().map(href -> {
            try {
                return sitesList.getSites().stream()
                        .filter(siteUrl -> siteUrl.getUrl().equals(url))
                        .findFirst()
                        .orElseThrow(() -> new OutOfSitesConfigurationException("Site not found"));
            } catch (OutOfSitesConfigurationException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    public Collection<EntityInterface> transformEntityToEntities (EntityInterface entity) {
        return Collections.singletonList(entity);
    }

    public SiteDto transformSiteModelToSiteDto(SiteModel siteModel) {
        return SiteDto.builder()
                .id(siteModel.getId())
                .url(siteModel.getUrl())
                .name(siteModel.getName())
                .status(siteModel.getStatus())
                .lastError(siteModel.getLastError())
                .version(siteModel.getVersion())
                .build();
    }

    public PageDto transformPageModelToPageDto(PageModel pageModel){
        return PageDto.builder()
                .id(pageModel.getId())
                .site(pageModel.getSite())
                .path(pageModel.getPath())
                .code(pageModel.getCode())
                .content(pageModel.getContent())
                .version(pageModel.getVersion())
                .build();
    }

    public LemmaDto transformLemmaModelToLemmaDto(LemmaModel lemmaModel){
        return LemmaDto.builder()
                .id(lemmaModel.getId())
                .site(lemmaModel.getSite())
                .lemma(lemmaModel.getLemma())
                .frequency(lemmaModel.getFrequency())
                .version(lemmaModel.getVersion())
                .build();
    }

    public IndexDto transferIndexModelToIndexDto(IndexModel indexModel){
        return IndexDto.builder()
                .id(indexModel.getId())
                .page(indexModel.getPage())
                .lemma(indexModel.getLemma())
                .rank(indexModel.getRank())
                .version(indexModel.getVersion())
                .build();
    }
}
