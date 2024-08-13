package searchengine.utils.dataTransformer.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.IndexDto;
import searchengine.model.IndexModel;

import java.util.Collection;
@Mapper(componentModel = "spring")
@Component
public interface IndexMapper {
    IndexModel dtoToModel(IndexDto indexDto);

    IndexDto modelToDto(IndexModel indexModel);

    Collection<IndexDto> toCollectionDto(Collection<IndexModel> indexModels);

    Collection<IndexModel> toCollectionModel(Collection<IndexDto> indexesDto);
}
