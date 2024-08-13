package searchengine.utils.dataTransformer.mapper;

import searchengine.dto.indexing.IndexDto;
import searchengine.dto.indexing.PageDto;
import searchengine.model.IndexModel;
import searchengine.model.PageModel;

import java.util.Collection;

public interface IndexMapper {
    IndexModel dtoToModel(IndexDto indexDto);

    IndexDto modelToDto(IndexModel indexModel);

    Collection<IndexDto> toCollectionDto(Collection<IndexModel> indexModels);

    Collection<IndexModel> toCollectionModel(Collection<IndexDto> indexesDto);
}
