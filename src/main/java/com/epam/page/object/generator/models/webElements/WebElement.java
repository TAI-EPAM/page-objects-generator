package com.epam.page.object.generator.models.webElements;

import org.jsoup.nodes.Element;

public interface WebElement {

    Element getElement();

    String getUniquenessValue();
}