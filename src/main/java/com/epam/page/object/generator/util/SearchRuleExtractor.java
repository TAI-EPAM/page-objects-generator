package com.epam.page.object.generator.util;

import com.epam.page.object.generator.model.Selector;
import com.epam.page.object.generator.model.searchrule.SearchRule;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.xsoup.Xsoup;

public class SearchRuleExtractor {

    private final static Logger logger = LoggerFactory.getLogger(SearchRuleExtractor.class);

    public SearchRuleExtractor() {
    }

    public Elements extractElementsFromElement(Element element, SearchRule searchRule) {
        Selector selector = searchRule.getSelector();
        if (selector.isXpath()) {
            return Xsoup.compile(selector.getValue()).evaluate(element).getElements();
        }
        else if(selector.isCss()) {
            return element.select(selector.getValue());
        }
        IllegalArgumentException e = new IllegalArgumentException(selector.toString());
        logger.error("Selector type is unknown " + selector.toString(), e);
        throw e;
    }
}
