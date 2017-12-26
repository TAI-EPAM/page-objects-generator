package com.epam.page.object.generator.util;

import com.epam.page.object.generator.model.Selector;
import com.epam.page.object.generator.model.searchrule.SearchRule;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.xsoup.Xsoup;

/**
 * This class responsible for extracting of inner elements {@link Elements} from element {@link Element}
 */
public class SearchRuleExtractor {

    public SearchRuleExtractor() {
    }

    /**
     * This method returns all inner elements {@link Elements} according to {@link SearchRule} rule.
     *
     * @param element {@link Element} that we are extracting elements from.
     * @param searchRule {@link SearchRule} is a rule that get suitable {@link Selector}
     * @return {@link Elements} from element according to searchRule
     *
     */
    public Elements extractElementsFromElement(Element element, SearchRule searchRule) {
        Selector selector = searchRule.getSelector();
        if (selector.isXpath()) {
            return Xsoup.compile(selector.getValue()).evaluate(element).getElements();
        } else if(selector.isCss()) {
            return element.select(selector.getValue());
        }
        throw new IllegalArgumentException("Selector type is unknown " + selector.toString());
    }
}
