package searchengine.utils.dataTransformer.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.LemmaDto;
import searchengine.dto.indexing.PageDto;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface PageMapper {
//    @Mapping(target="employeeId", source = "entity.id")
//    @Mapping(target="employeeName", source = "entity.name")
//    @Mapping(target="employeeStartDt", source = "entity.startDt",
//            dateFormat = "dd-MM-yyyy HH:mm:ss")
    PageModel dtoToModel(PageDto pageDto);

    PageDto modelToDto(PageModel pageModel);

    Collection<PageDto> toCollectionDto(Collection<PageModel> pageModels);

    Collection<PageModel> toCollectionModel(Collection<PageDto> pagesDto);
}
