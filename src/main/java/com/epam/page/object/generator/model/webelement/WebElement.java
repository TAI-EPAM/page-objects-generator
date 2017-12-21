package com.epam.page.object.generator.model.webelement;

import org.jsoup.nodes.Element;

/**
 * Interface for elements used as parts of {@link com.epam.page.object.generator.model.WebPage}:
 * {@link CommonWebElement}
 * {@link ComplexWebElement}
 * {@link FormWebElement}
 */
public interface WebElement {

    Element getElement();

    String getUniquenessValue();
}
