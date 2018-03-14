package com.epam.page.object.generator.model.searchrule;

import com.codeborne.selenide.ElementsCollection;
import com.epam.page.object.generator.databuilder.searchrule.SelenideElementsCollectionSearchRuleTestDataBuilder;
import com.epam.page.object.generator.model.ClassAndAnnotationPair;
import com.epam.page.object.generator.model.Selector;
import com.epam.page.object.generator.util.SearchRuleType;
import com.epam.page.object.generator.util.SelectorUtils;
import com.epam.page.object.generator.util.XpathToCssTransformer;
import org.junit.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.Assert.assertEquals;

public class SelenideElementsCollectionSearchRuleTest {

    private XpathToCssTransformer xpathToCssTransformer = new XpathToCssTransformer();
    private SelectorUtils selectorUtils = new SelectorUtils();

    private SelenideElementsCollectionSearchRule selenideElementsCollectionSearchRule;

    @Test
    public void getRuleWithUniquenessValueSelector_TransformedSelector_Valid() {
        selenideElementsCollectionSearchRule = SelenideElementsCollectionSearchRuleTestDataBuilder
                .getSelenideSearchRule_UniquenessValue_SelectorCss();

        Selector transformedSelector = selenideElementsCollectionSearchRule.getTransformedSelector();
        assertEquals("css", transformedSelector.getType());
        assertEquals("input[type=submit]", transformedSelector.getValue());
    }

    @Test
    public void getRuleWithUniquenessValueSelectorCss_TransformedSelector_Valid() {
        selenideElementsCollectionSearchRule = SelenideElementsCollectionSearchRuleTestDataBuilder
                .getSelenideSearchRule_UniquenessValue_SelectorCss();

        Selector transformedSelector = selenideElementsCollectionSearchRule.getTransformedSelector();
        assertEquals("css", transformedSelector.getType());
        assertEquals("input[type=submit]", transformedSelector.getValue());
    }

    @Test
    public void getRuleWithUniquenessTextSelectorCss_TransformedSelector_Valid() {
        selenideElementsCollectionSearchRule = SelenideElementsCollectionSearchRuleTestDataBuilder
                .getSelenideSearchRule_UniquenessText_SelectorCss();

        Selector transformedSelector = selenideElementsCollectionSearchRule.getTransformedSelector();
        assertEquals("css", transformedSelector.getType());
        assertEquals("input[type=submit]", transformedSelector.getValue());
    }

    @Test
    public void build_CommonSearchRuleWithInvalidCssSelector() {
        selenideElementsCollectionSearchRule = new SelenideElementsCollectionSearchRule(
                "collection", "value", SearchRuleType.ELEMENTS_COLLECTION,
                new Selector("css", "Invalid_CSS"),
                new ClassAndAnnotationPair(ElementsCollection.class, FindBy.class), xpathToCssTransformer,
                selectorUtils, true);

        Selector transformedSelector = selenideElementsCollectionSearchRule.getTransformedSelector();
        assertEquals("css", transformedSelector.getType());
        assertEquals("Invalid_CSS", transformedSelector.getValue());
    }
}