package com.epam.page.object.generator.model.webelement;

import com.epam.page.object.generator.model.searchrule.ComplexInnerSearchRule;
import org.jsoup.nodes.Element;

/**
 * Contains information required to find complex web elements with
 * list of simple inner elements
 */
public class ComplexWebElement implements WebElement {

    private Element element;
    private String uniquenessValue;
    /**
     * Search rule required to from list of inner elements
     */
    private ComplexInnerSearchRule searchRule;

    public ComplexWebElement(Element element, String uniquenessValue,
                             ComplexInnerSearchRule searchRule) {
        this.element = element;
        this.uniquenessValue = uniquenessValue;
        this.searchRule = searchRule;
    }

    public ComplexInnerSearchRule getSearchRule() {
        return searchRule;
    }

    @Override
    public Element getElement() {
        return element;
    }

    @Override
    public String getUniquenessValue() {
        return uniquenessValue;
    }

    @Override
    public String toString() {
        return "WebElement{" + element + '}';
    }
}
