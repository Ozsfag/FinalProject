package searchengine.utils.dataTransformer.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.LemmaDto;
import searchengine.model.LemmaModel;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface LemmaMapper {
    LemmaModel dtoToModel(LemmaDto lemmaDto);

    LemmaDto modelToDto(LemmaModel lemmaModel);

    Collection<LemmaDto> toCollectionDto(Collection<LemmaModel> lemmaModels);
    Collection<LemmaModel> toCollectionModel(Collection<LemmaDto> lemmasDto);
}
