package searchengine.utils.searching.snippetTransmitter.contentMatcher.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import searchengine.utils.searching.snippetTransmitter.contentMatcher.ContentMatcher;

@Component
@Lazy
public class ContentMatcherImpl implements ContentMatcher {
  @Override
  public String match(String content, String word) {
    Matcher matcher = getMatcher(content, word);
    String matchingSentence = null;
    while (matcher.find()) {
      int start = Math.max(matcher.start() - 100, 0);
      int end = Math.min(matcher.end() + 100, content.length());
      //      word = matcher.group();
      matchingSentence = content.substring(start, end);
    }
    return matchingSentence;
  }

  private Matcher getMatcher(String content, String word) {
    return Pattern.compile(Pattern.quote(word)).matcher(content);
  }
}
