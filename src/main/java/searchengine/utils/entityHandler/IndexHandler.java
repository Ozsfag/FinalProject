package searchengine.utils.entityHandler;

import java.util.Collection;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.IndexDto;
import searchengine.dto.indexing.LemmaDto;
import searchengine.dto.indexing.PageDto;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.repositories.IndexRepository;
import searchengine.utils.dataTransformer.mapper.IndexMapper;
import searchengine.utils.dataTransformer.mapper.LemmaMapper;
import searchengine.utils.dataTransformer.mapper.PageMapper;
import searchengine.utils.entityFactory.EntityFactory;

@Component
@RequiredArgsConstructor
public class IndexHandler {
  private final IndexRepository indexRepository;
  private final EntityFactory entityFactory;
  private final LemmaMapper lemmaMapper;
  private final IndexMapper indexMapper;
  private final PageMapper pageMapper;

  private PageDto pageDto;
  private Collection<LemmaDto> lemmasDto;
  private Collection<IndexDto> existingIndexModels;

  public Collection<IndexModel> getIndexedIndexModelFromCountedWords(
          PageDto pageDto, Collection<LemmaDto> lemmasDto) {
    this.pageDto = pageDto;
    this.lemmasDto = lemmasDto;

    getExistingIndexes();
    removeExistedIndexesFromNew();
    existingIndexModels.addAll(createNewFromNotExisted());

    return indexMapper.toCollectionModel(existingIndexModels);
  }

  private void getExistingIndexes() {
    Collection<LemmaModel>  lemmaModels = lemmaMapper.toCollectionModel(lemmasDto);
    Collection<IndexModel> indexes = indexRepository.findByPage_IdAndLemmaIn(pageDto.getId(), lemmaModels);
    existingIndexModels = indexMapper.toCollectionDto(indexes);
  }

  private void removeExistedIndexesFromNew() {
    lemmasDto.removeIf(
        lemmaDto ->
            existingIndexModels.parallelStream()
                .map(IndexDto::getLemma)
                .toList()
                .contains(lemmaDto.getLemma()));
  }

  private Collection<IndexDto> createNewFromNotExisted() {
    Collection<LemmaModel> lemmaModel = lemmaMapper.toCollectionModel(lemmasDto);
    PageModel pageModel = pageMapper.dtoToModel(pageDto);
    Collection<IndexModel> indexes = lemmaModel.parallelStream()
            .map(
                    lemma -> entityFactory.createIndexModel(pageModel, lemma, (float) lemma.getFrequency()))
            .collect(Collectors.toSet());
    return indexMapper.toCollectionDto(indexes);
  }
}
