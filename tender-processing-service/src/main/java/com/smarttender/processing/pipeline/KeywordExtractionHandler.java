package com.smarttender.processing.pipeline;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;
@Component @Order(3)
public class KeywordExtractionHandler extends AbstractProcessingHandler {
    private static final Set<String> STOP_WORDS = Set.of(
        "the","and","for","are","but","not","you","all","any","can","had","her","was",
        "one","our","out","day","get","has","him","his","how","man","new","now","old",
        "see","two","way","who","boy","did","its","let","put","say","she","too","use",
        "of","to","in","is","it","be","as","at","so","we","he","by","or","on","do",
        "if","me","my","up","an","go","no","us","am","a"
    );
    @Override public int getOrder() { return 3; }
    @Override
    protected void doHandle(ProcessingContext ctx) {
        var raw = ctx.getRawEvent();
        List<String> keywords = extractKeywords(raw.getTitle() + " " + raw.getOrganisationName());
        ctx.getBuilder().extractedKeywords(keywords);
    }
    private List<String> extractKeywords(String text) {
        if (text == null) return Collections.emptyList();
        return Arrays.stream(text.toLowerCase().split("[^a-zA-Z0-9]+"))
                .filter(w -> w.length() > 3 && !STOP_WORDS.contains(w))
                .distinct().limit(15).collect(Collectors.toList());
    }
}
