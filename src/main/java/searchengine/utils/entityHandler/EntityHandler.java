package searchengine.utils.entityHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.Site;
import searchengine.exceptions.StoppedExecutionException;
import searchengine.model.*;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.utils.entityFactory.EntityFactory;
import searchengine.utils.morphology.Morphology;

import java.util.*;
import java.util.stream.Collectors;

import static searchengine.services.indexing.IndexingImpl.isIndexing;

/**
 * Util that handle and process kind of entities
 * @author Ozsfag
 */
@Component
@RequiredArgsConstructor
public class EntityHandler {
    private final SiteRepository siteRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    public final Morphology morphology;
    private final PageRepository pageRepository;
    private final EntityFactory entityFactory;

    public Collection<SiteModel> getIndexedSiteModelFromSites(Collection<Site> sitesToParse) {
        return sitesToParse.stream().map(entityFactory::createSiteModel).toList();
    }

    /**
     * Indexes the lemmas and indexes for a list of pages.
     *
     * @param urlsToParse the list of pages to index
     * @param siteModel
     */
    public void processIndexing(Collection<String> urlsToParse, SiteModel siteModel) {
        Collection<PageModel> pages = getIndexedPageModelsFromUrls(urlsToParse, siteModel);
        saveEntities(pages);

        pages.forEach(page -> {
            Map<String, Integer> wordCountMap = morphology.wordCounter(page.getContent());
            Collection<LemmaModel> lemmas = getIndexedLemmaModelsFromCountedWords(siteModel, wordCountMap);
            saveEntities(lemmas);
            Collection<IndexModel> indexes = getIndexedIndexModelFromCountedWords(page, lemmas, wordCountMap);
            saveEntities(indexes);
            siteRepository.updateStatusTimeByUrl(new Date(), siteModel.getUrl());
        });
    }
    /**
     * Retrieves the indexed PageModel list from the list of URLs.
     *
     * @param urlsToParse  the list of URLs to parse
     * @param siteModel    the SiteModel containing the content
     * @return              the set of indexed PageModels
     */
    public Set<PageModel> getIndexedPageModelsFromUrls(Collection<String> urlsToParse, SiteModel siteModel) {
        return urlsToParse.parallelStream()
                .map(url -> {
                    PageModel pageModel = entityFactory.createPageModel(siteModel, url);
                    if (!isIndexing) {
                        throw new StoppedExecutionException("Индексация остановлена пользователем");
                    }
                    return pageModel;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * Retrieves the indexed LemmaModel list from the content of a SiteModel.
     *
     * @param  siteModel    the SiteModel containing the content
     * @param  wordCountMap a map of word frequencies in the content
     * @return              the set of indexed LemmaModels
     */
    public Collection<LemmaModel> getIndexedLemmaModelsFromCountedWords(SiteModel siteModel, Map<String, Integer> wordCountMap) {

        Collection<LemmaModel> existingLemmaModels =
                lemmaRepository.findByLemmaInAndSite_Id(wordCountMap.keySet(), siteModel.getId())
                        .parallelStream()
                        .collect(Collectors.toSet());

        wordCountMap.entrySet().removeIf(entry -> existingLemmaModels.parallelStream()
                .map(LemmaModel::getLemma)
                .toList()
                .contains(entry.getKey()));

        existingLemmaModels.addAll(wordCountMap.entrySet().stream()
                .map(entry -> entityFactory.createLemmaModel(siteModel, entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet())
        );

        return existingLemmaModels;
    }

    /**
     * Retrieves the IndexModel list from the content of a PageModel.
     *
     * @param  pageModel    the PageModel to retrieve indexes from
     * @param  lemmas       the set of LemmaModels to search for in the content
     * @param  wordCountMap a map of word frequencies in the content
     * @return the list of IndexModels generated from the content
     */
    public Collection<IndexModel> getIndexedIndexModelFromCountedWords(PageModel pageModel, Collection<LemmaModel> lemmas, Map<String, Integer> wordCountMap) {
        Collection<IndexModel> existingIndexModels = indexRepository.findByPage_IdAndLemmaIn(pageModel.getId(), lemmas)
                .parallelStream()
                .collect(Collectors.toSet());

        wordCountMap.entrySet().removeIf(entry-> existingIndexModels.parallelStream()
                .map(IndexModel::getLemma)
                .toList()
                .contains(lemmas.stream()
                        .filter(lemma -> lemma.getLemma().equals(entry.getKey()))
                        .findFirst()
                ));

        existingIndexModels.addAll(wordCountMap.entrySet().parallelStream()
                .map(word2Count -> {
                        try {
                           LemmaModel lemmaModel = lemmas.stream().filter(lemma -> lemma.getLemma().equals(word2Count.getKey())).findFirst().get();
                           return entityFactory.createIndexModel(pageModel, lemmaModel, (float) word2Count.getValue());
                        } catch (StoppedExecutionException e) {
                            throw new RuntimeException(e.getLocalizedMessage());
                        }
                    })
                .collect(Collectors.toSet())
        );

        return existingIndexModels;
    }
    /**
     * Saves a set of entities to the database using the provided JpaRepository.
     * If an exception occurs during the save operation, the entities are saved individually
     * using the respective repository merge methods.
     *
     * @param entities the set of entities to save
     */
    public void saveEntities(Collection<?> entities) {
        try {
            Class<?> repositoryClass = entities.iterator().next().getClass();
            switch (repositoryClass.getSimpleName()) {
                case "SiteModel":
                    Collection<SiteModel> sites = (Collection<SiteModel>) entities;
                    siteRepository.saveAll(sites);
                    break;
                case "PageModel":
                    Collection<PageModel> pages = (Collection<PageModel>) entities;
                    pageRepository.saveAll(pages);
                    break;
                case "LemmaModel":
                    Collection<LemmaModel> lemmas = (Collection<LemmaModel>) entities;
                    lemmaRepository.saveAll(lemmas);
                    break;
                case "IndexModel":
                    Collection<IndexModel> indexes = (Collection<IndexModel>) entities;
                    indexRepository.saveAll(indexes);
                    break;
            }
        } catch (Exception e) {
            entities.forEach(entity -> {
                Class<?> aClass = entity.getClass();
                switch (aClass.getSimpleName()) {
                    case "SiteModel":
                        SiteModel siteModel = (SiteModel) entity;
                        siteRepository.saveAndFlush(siteModel);
                        break;
                    case "PageModel":
                        PageModel pageModel = (PageModel) entity;
                        pageRepository.merge(pageModel.getId(), pageModel.getCode(), pageModel.getSite().getId(), pageModel.getContent(), pageModel.getPath(), pageModel.getVersion());
                        break;
                    case "LemmaModel":
                        LemmaModel lemmaModel = (LemmaModel) entity;
                        lemmaRepository.merge(lemmaModel.getLemma(), lemmaModel.getSite().getId(), lemmaModel.getFrequency());
                        break;
                    case "IndexModel":
                        IndexModel indexModel = (IndexModel) entity;
                        indexRepository.merge(indexModel.getLemma().getLemma(), indexModel.getPage().getId(), indexModel.getRank());
                        break;
                }
            });
        }
    }
}