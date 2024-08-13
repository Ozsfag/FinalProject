package searchengine.utils.dataTransformer.mapper;

import org.mapstruct.Mapper;
import searchengine.dto.indexing.LemmaDto;
import searchengine.dto.indexing.PageDto;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PageMapper {
    PageModel dtoToModel(PageDto pageDto);

    PageDto modelToDto(PageModel pageModel);

    Collection<PageDto> toCollectionDto(Collection<PageModel> pageModels);

    Collection<PageModel> toCollectionModel(Collection<PageDto> pagesDto);
}
