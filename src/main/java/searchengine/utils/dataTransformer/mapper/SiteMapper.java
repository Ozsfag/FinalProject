package searchengine.utils.dataTransformer.mapper;

import org.mapstruct.Mapper;
import searchengine.dto.indexing.SiteDto;
import searchengine.model.SiteModel;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface SiteMapper {
    SiteModel dtoToModel(SiteDto siteDto);

    SiteDto modelToDto(SiteModel siteModel);

    Collection<SiteDto> toCollectionDto(Collection<SiteModel> siteModels);
}
