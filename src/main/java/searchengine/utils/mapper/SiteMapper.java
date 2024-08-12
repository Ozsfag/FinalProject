package searchengine.utils.mapper;

import org.mapstruct.Mapper;
import searchengine.dto.indexing.SiteDto;
import searchengine.model.SiteModel;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SiteMapper {
    SiteModel dtoToModel(SiteDto siteDto);

    SiteDto modelToDto(SiteModel siteModel);

    List<SiteDto> toListDto(List<SiteModel> siteModels);
}
