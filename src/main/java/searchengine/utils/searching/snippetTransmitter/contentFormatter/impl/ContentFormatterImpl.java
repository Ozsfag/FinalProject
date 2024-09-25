package searchengine.utils.searching.snippetTransmitter.contentFormatter.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.utils.searching.snippetTransmitter.contentFormatter.ContentFormatter;

@Component
@Lazy
public class ContentFormatterImpl implements ContentFormatter {
  @Override
  public String format(String matchingSentence, String word) {
    if (matchingSentence != null) {
      return matchingSentence.replaceAll(word, "<b>" + word + "</b>");
    }
    return null;
  }
}
