package com.epam.page.object.generator.model.webelement;

import com.epam.page.object.generator.model.searchrule.CommonSearchRule;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * This class represents {@link Element} which was found in the web site by the {@link
 * CommonSearchRule} and its 'uniqueness' value from this element
 */
public class ListWebElement implements com.epam.page.object.generator.model.webelement.WebElement {

    private Element element;

    public ListWebElement(Element element) {
        this.element = element;
    }

    @Override
    public Element getElement() {
        return element;
    }

    @Override
    public String getUniquenessValue() {
        return null;
    }

    public static TypeName getType() {
        return ParameterizedTypeName.get(List.class, WebElement.class);
    }

    @Override
    public String toString() {
        return "WebElement{" + element + '}';
    }
}
