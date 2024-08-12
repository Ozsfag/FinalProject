package searchengine.utils.mapper;

import searchengine.dto.indexing.IndexDto;
import searchengine.model.IndexModel;

import java.util.List;

public interface IndexMapper {
    IndexModel dtoToModel(IndexDto indexDto);

    IndexDto modelToDto(IndexModel indexModel);

    List<IndexDto> toListDto(List<IndexModel> indexModels);
}
