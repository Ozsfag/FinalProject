package searchengine.utils.searching.snippetTransmitter.contentFormatter.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.utils.searching.snippetTransmitter.contentFormatter.ContentFormatter;

@Component
@Lazy
public class ContentFormatterImpl implements ContentFormatter {
  @Override
  public String format(String matchedWord) {
    if (matchedWord == null)
      return null;
    return matchedWord.replace(matchedWord, "<b>" + matchedWord + "</b>");
  }
}
