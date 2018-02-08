package com.epam.page.object.generator.util;

import com.epam.page.object.generator.model.Selector;
import com.epam.page.object.generator.model.searchrule.ComplexInnerSearchRule;
import com.epam.page.object.generator.model.searchrule.ComplexSearchRule;
import com.epam.page.object.generator.model.searchrule.SearchRule;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.xsoup.Xsoup;

import java.util.Iterator;
import java.util.List;

/**
 * This class responsible for extracting of inner {@link Elements} from {@link Element}.
 */
public class SearchRuleExtractor {

    public SearchRuleExtractor() {
    }

    /**
     * This method returns all inner {@link Elements} according to {@link SearchRule}.
     *
     * @param element    {@link Element} that we are extracting from {@link Elements}.
     * @param searchRule {@link SearchRule} is a rule that get suitable {@link Selector}.
     * @return {@link Elements} from {@link Element} according to {@link SearchRule}
     */
    public Elements extractElementsFromElement(Element element, SearchRule searchRule) {
        Elements elements = extractElementsFromDocument(element, searchRule);
        if (searchRule instanceof ComplexSearchRule) {
            this.checkInnerElements(elements, searchRule);
        }
        return elements;
    }

    private Elements extractElementsFromDocument(Element element, SearchRule searchRule) {
        Selector selector = searchRule.getSelector();
        if (selector.isXpath()) {
            return Xsoup.compile(selector.getValue()).evaluate(element).getElements();
        } else if (selector.isCss()) {
            return element.select(selector.getValue());
        }
        throw new IllegalArgumentException("Selector type is unknown " + selector.toString());
    }

    private void checkInnerElements(Elements elements, SearchRule searchRule) {
        List<ComplexInnerSearchRule> rules = ((ComplexSearchRule) searchRule).getInnerSearchRules();

        for (Element element : elements) {
            Element document = element.parent();
            try {
                rules = rules.subList(1, rules.size());
            } catch (IndexOutOfBoundsException e) {
                break;
            }
            Iterator<ComplexInnerSearchRule> iterator = rules.iterator();

            while (iterator.hasNext()) {
                Elements innerElements = extractElementsFromDocument(document, iterator.next());
                if (innerElements.size() == 0) {
                    iterator.remove();
                }
            }
        }
    }
}
