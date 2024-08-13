package searchengine.utils.dataTransformer.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import searchengine.dto.indexing.LemmaDto;
import searchengine.dto.indexing.SiteDto;
import searchengine.model.LemmaModel;
import searchengine.model.SiteModel;

import java.util.Collection;

@Mapper(componentModel = "spring")
@Component
public interface SiteMapper {
    SiteModel dtoToModel(SiteDto siteDto);

    SiteDto modelToDto(SiteModel siteModel);

    Collection<SiteDto> toCollectionDto(Collection<SiteModel> siteModels);

    Collection<SiteModel> toCollectionModel(Collection<SiteDto> sitesDto);
}
