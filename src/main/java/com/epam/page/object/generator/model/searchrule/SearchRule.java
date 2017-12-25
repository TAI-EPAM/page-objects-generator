package com.epam.page.object.generator.model.searchrule;

import com.epam.page.object.generator.model.Selector;
import com.epam.page.object.generator.model.webgroup.WebElementGroup;
import com.epam.page.object.generator.model.webelement.WebElement;
import java.util.List;
import org.jsoup.select.Elements;

/**
 * {@link SearchRule} is a rule containing certain JSON and based on which the element is
 * searched on the HTML page.
 * {@link SearchRule} interface extends {@link Validatable} interface  that is responsible for
 * verifying the correctness of the {@link SearchRule}.
 * This is the main interface that you should implement to create a new {@link SearchRule}.
 */
public interface SearchRule extends Validatable {

    /**
     * @return This method returns the {@link Selector} of particular SearchRule
     */
    Selector getSelector();

    /**
     * This method wraps the jsoup {@link org.jsoup.nodes.Element}s in list of {@link WebElement}s
     * @param elements Which jsoup parse from HTML page
     * @return List of {@link WebElement}s
     */
    List<WebElement> getWebElements(Elements elements);

    /**
     * This method fills list of {@link WebElementGroup} with {@link org.jsoup.nodes.Element}s that
     * were found by certain search rules (that are included in definite group) on the HTML page
     * @param webElementGroups List in which will be written elements which belong to a certain
     *                        group of web elements
     * @param elements Which jsoup parse from HTML page
     */
    void fillWebElementGroup(List<WebElementGroup> webElementGroups,
                             Elements elements);
}
