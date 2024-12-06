package searchengine.utils.searching.snippetTransmitter.contentMatcher.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.utils.searching.snippetTransmitter.contentFormatter.ContentFormatter;
import searchengine.utils.searching.snippetTransmitter.contentMatcher.ContentMatcher;

@Component
@Lazy
public class ContentMatcherImpl implements ContentMatcher {
  private final ContentFormatter contentFormatter;

  public ContentMatcherImpl(ContentFormatter contentFormatter) {
    this.contentFormatter = contentFormatter;
  }

  @Override
  public String match(String content, String word) {
    Matcher matcher = getMatcher(content, word);
    StringBuilder matchingSentences = new StringBuilder();

    while (matcher.find()) {
      String formattedWord = getFormattedWord(matcher);
      String context = getContext(content, matcher.start(), matcher.end(), formattedWord);
      matchingSentences.append(context).append("|");
    }

    return matchingSentences.toString().trim();
  }

  private Matcher getMatcher(String content, String word) {
    String regex = "(?U)" + Pattern.quote(word) + "\\b";
    return Pattern.compile(regex).matcher(content);
  }

  private String getFormattedWord(Matcher matcher) {
    return contentFormatter.format(matcher.group());
  }

  private String getContext(String content, int start, int end, String formattedWord) {
    int head = getHeadIndex(start);
    int tail = getTailIndex(end, content.length());

    return content.substring(head, start) + formattedWord + content.substring(end, tail);
  }

  private int getHeadIndex(int start) {
    return Math.max(start - 100, 0);
  }

  private int getTailIndex(int end, int contentLength) {
    return Math.min(end + 100, contentLength);
  }
}
