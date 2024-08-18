package searchengine.utils.parser;

import java.util.*;
import java.util.concurrent.RecursiveTask;
import lombok.RequiredArgsConstructor;
import searchengine.model.SiteModel;
import searchengine.utils.entityHandler.EntityHandler;

/**
 * Recursively index page and it`s subpage.
 *
 * @author Ozsfag
 */
@RequiredArgsConstructor
public class Parser extends RecursiveTask<Boolean> {
  private final EntityHandler entityHandler;
  private final SiteModel siteModel;
  private final String href;


  @Override
  protected Boolean compute() {
    entityHandler.indexingUrl(href, siteModel);
    invokeAll(getSubtasks(href, siteModel));
    return true;
  }


  private Collection<Parser> getSubtasks(String href, SiteModel siteModel) {
    return entityHandler.checkingUrls(href, siteModel).parallelStream().map(this::createParser).toList();
  }


  private Parser createParser(String url) {
    return new Parser(entityHandler, siteModel, url);
  }
}