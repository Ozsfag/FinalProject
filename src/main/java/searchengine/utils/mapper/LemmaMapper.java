package searchengine.utils.mapper;

import org.mapstruct.Mapper;
import searchengine.dto.indexing.LemmaDto;
import searchengine.model.LemmaModel;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LemmaMapper {
    LemmaModel dtoToModel(LemmaDto lemmaDto);

    LemmaDto modelToDto(LemmaModel lemmaModel);

    List<LemmaDto> toListDto(List<LemmaModel> lemmaModels);
}
