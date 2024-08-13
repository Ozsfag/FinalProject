package searchengine.utils.dataTransformer.mapper;

import searchengine.dto.indexing.IndexDto;
import searchengine.model.IndexModel;

import java.util.Collection;

public interface IndexMapper {
    IndexModel dtoToModel(IndexDto indexDto);

    IndexDto modelToDto(IndexModel indexModel);

    Collection<IndexDto> toCollectionDto(Collection<IndexModel> indexModels);
}
