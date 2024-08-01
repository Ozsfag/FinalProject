package searchengine.utils.entityHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.repositories.IndexRepository;
import searchengine.utils.entityFactory.EntityFactory;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IndexHandler {
    private final IndexRepository indexRepository;
    private final EntityFactory entityFactory;

    private PageModel pageModel;
    private Collection<LemmaModel> lemmas;
    private Collection<IndexModel> existingIndexModels;

    public Collection<IndexModel> getIndexedIndexModelFromCountedWords(PageModel pageModel, Collection<LemmaModel> lemmas) {
        this.pageModel = pageModel;
        this.lemmas = lemmas;

        getExistingIndexes();
        removeExistedIndexesFromNew();
        existingIndexModels.addAll(createNewFromNotExisted());

        return existingIndexModels;
    }
    private void getExistingIndexes(){
        existingIndexModels  = indexRepository.findByPage_IdAndLemmaIn(pageModel.getId(), lemmas);
    }
    private void removeExistedIndexesFromNew(){
        lemmas.removeIf(lemma -> existingIndexModels.parallelStream()
                .map(IndexModel::getLemma)
                .toList()
                .contains(lemma.getLemma()));
    }
    private Collection<IndexModel> createNewFromNotExisted(){
        return lemmas.parallelStream()
                .map(lemma -> entityFactory.createIndexModel(pageModel, lemma,(float) lemma.getFrequency()))
                .collect(Collectors.toSet());
    }







}
