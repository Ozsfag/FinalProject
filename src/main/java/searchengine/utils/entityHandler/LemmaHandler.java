package searchengine.utils.entityHandler;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.LemmaDto;
import searchengine.dto.indexing.SiteDto;
import searchengine.model.LemmaModel;
import searchengine.model.SiteModel;
import searchengine.repositories.LemmaRepository;
import searchengine.utils.dataTransformer.mapper.LemmaMapper;
import searchengine.utils.dataTransformer.mapper.SiteMapper;
import searchengine.utils.entityFactory.EntityFactory;

@Component
@RequiredArgsConstructor
public class LemmaHandler {
  private final LemmaRepository lemmaRepository;
  private final EntityFactory entityFactory;
  private final SiteMapper siteMapper;
  private final LemmaMapper lemmaMapper;

  private SiteDto siteDto;
  private Map<String, Integer> wordsCount;
  private Collection<LemmaDto> existingLemmaModels;

  public Collection<LemmaModel> getIndexedLemmaModelsFromCountedWords(
          SiteDto siteDto, Map<String, Integer> wordsCount) {
    this.siteDto = siteDto;
    this.wordsCount = wordsCount;

    getExistingLemmas();
    removeExistedLemmasFromNew();
    existingLemmaModels.addAll(createNewFromNotExisted());

    return lemmaMapper.toCollectionModel(existingLemmaModels);
  }

  private void getExistingLemmas() {
    Collection<LemmaModel> lemmas = lemmaRepository.findByLemmaInAndSite_Id(wordsCount.keySet(), siteDto.getId());
    existingLemmaModels = lemmaMapper.toCollectionDto(lemmas);
  }

  private void removeExistedLemmasFromNew() {
    wordsCount
        .entrySet()
        .removeIf(
            entry ->
                existingLemmaModels.parallelStream()
                    .map(LemmaDto::getLemma)
                    .toList()
                    .contains(entry.getKey()));
  }

  private Collection<LemmaDto> createNewFromNotExisted() {
    SiteModel siteModel = siteMapper.dtoToModel(siteDto);
    Collection<LemmaModel> newLemmas = wordsCount.entrySet().parallelStream()
            .map(entry -> entityFactory.createLemmaModel(siteModel, entry.getKey(), entry.getValue()))
            .collect(Collectors.toSet());
    return lemmaMapper.toCollectionDto(newLemmas);
  }
}
