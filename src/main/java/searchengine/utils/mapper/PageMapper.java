package searchengine.utils.mapper;

import org.mapstruct.Mapper;
import searchengine.dto.indexing.PageDto;
import searchengine.model.PageModel;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PageMapper {
    PageModel dtoToModel(PageDto pageDto);

    PageDto modelToDto(PageModel pageModel);

    List<PageDto> toListDto(List<PageModel> pageModels);
}
