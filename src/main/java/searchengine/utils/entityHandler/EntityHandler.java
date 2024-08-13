package searchengine.utils.entityHandler;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.LemmaDto;
import searchengine.dto.indexing.PageDto;
import searchengine.dto.indexing.SiteDto;
import searchengine.model.*;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.utils.dataTransformer.mapper.LemmaMapper;
import searchengine.utils.dataTransformer.mapper.PageMapper;
import searchengine.utils.morphology.Morphology;

/**
 * Util that handle and process kind of entities
 *
 * @author Ozsfag
 */
@Component
@RequiredArgsConstructor
public class EntityHandler {
  private final SiteRepository siteRepository;
  private final LemmaRepository lemmaRepository;
  private final IndexRepository indexRepository;
  private final Morphology morphology;
  private final PageRepository pageRepository;
  private final LemmaHandler lemmaHandler;
  private final IndexHandler indexHandler;
  private final PageHandler pageHandler;
  private final PageMapper pageMapper;
  private final LemmaMapper lemmaMapper;

  /**
   * Indexes the lemmas and indexes for a list of pages.
   *
   * @param urlsToParse the list of pages to index
   * @param siteDto
   */
  public void processIndexing(Collection<String> urlsToParse, SiteDto siteDto) {
    Collection<PageModel> pages = pageHandler.getIndexedPageModelsFromUrls(urlsToParse, siteDto);
    saveEntities(pages);
    Collection<PageDto> pagesDto = pageMapper.toCollectionDto(pages);

    pagesDto.forEach(
        pageDto -> {
          Map<String, Integer> wordsCount = morphology.countWordFrequencyByLanguage(pageDto.getContent());
          Collection<LemmaModel> lemmas =
              lemmaHandler.getIndexedLemmaModelsFromCountedWords(siteDto, wordsCount);
          saveEntities(lemmas);
          Collection<LemmaDto> lemmasDto = lemmaMapper.toCollectionDto(lemmas);
          Collection<IndexModel> indexes =
              indexHandler.getIndexedIndexModelFromCountedWords(pageDto, lemmasDto);
          saveEntities(indexes);
          siteRepository.updateStatusTimeByUrl(new Date(), siteDto.getUrl());
        });
  }

  /**
   * Saves a set of entities to the database using the provided JpaRepository. If an exception
   * occurs during the save operation, the entities are saved individually using the respective
   * repository merge methods.
   *
   * @param entities the set of entities to save
   */
  public void saveEntities(Collection<?> entities) {
    try {
      Class<?> repositoryClass = entities.iterator().next().getClass();
      switch (repositoryClass.getSimpleName()) {
        case "SiteModel":
          Collection<SiteModel> sites = (Collection<SiteModel>) entities;
          siteRepository.saveAllAndFlush(sites);
          break;
        case "PageModel":
          Collection<PageModel> pages = (Collection<PageModel>) entities;
          pageRepository.saveAllAndFlush(pages);
          break;
        case "LemmaModel":
          Collection<LemmaModel> lemmas = (Collection<LemmaModel>) entities;
          lemmaRepository.saveAllAndFlush(lemmas);
          break;
        case "IndexModel":
          Collection<IndexModel> indexes = (Collection<IndexModel>) entities;
          indexRepository.saveAllAndFlush(indexes);
          break;
      }
    } catch (Exception e) {
      entities.forEach(
          entity -> {
            Class<?> aClass = entity.getClass();
            switch (aClass.getSimpleName()) {
              case "SiteModel":
                SiteModel siteModel = (SiteModel) entity;
                if (siteRepository.existsByUrl(siteModel.getUrl())) break;
                siteRepository.saveAndFlush(siteModel);
                break;
              case "PageModel":
                PageModel pageModel = (PageModel) entity;
                if (pageRepository.existsByPath(pageModel.getPath())) break;
                pageRepository.merge(
                    pageModel.getId(),
                    pageModel.getCode(),
                    pageModel.getSite().getId(),
                    pageModel.getContent(),
                    pageModel.getPath(),
                    pageModel.getVersion());
                break;
              case "LemmaModel":
                LemmaModel lemmaModel = (LemmaModel) entity;
                if (lemmaRepository.existsByLemma(lemmaModel.getLemma())) break;
                lemmaRepository.merge(
                    lemmaModel.getLemma(), lemmaModel.getSite().getId(), lemmaModel.getFrequency());
                break;
              case "IndexModel":
                IndexModel indexModel = (IndexModel) entity;
                if (indexRepository.existsByPage_IdAndLemma_Id(
                    indexModel.getPage().getId(), indexModel.getLemma().getId())) break;
                indexRepository.merge(
                    indexModel.getLemma().getLemma(),
                    indexModel.getPage().getId(),
                    indexModel.getRank());
                break;
            }
          });
    }
  }
}
