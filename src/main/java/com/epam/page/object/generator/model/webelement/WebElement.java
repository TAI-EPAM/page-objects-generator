package com.epam.page.object.generator.model.webelement;

import com.epam.page.object.generator.model.WebPage;
import com.epam.page.object.generator.model.searchrule.SearchRule;
import org.jsoup.nodes.Element;

/**
 * Interface for elements used as parts of {@link WebPage}:
 *     <br>
 * <ul>
 * <li>{@link CommonWebElement}</li>
 * <li>{@link ComplexWebElement}</li>
 * <li>{@link FormWebElement}</li>
 * </ul>
 * Implementations of this interface wrap jsoup {@link Element} with
 * uniqueness value from {@link SearchRule}
 */
public interface WebElement {


    /**
     * @return basic info of web element
     */
    Element getElement();

    /**
     * @return value of attribute that was specified in search rule
     */
    String getUniquenessValue();
}
