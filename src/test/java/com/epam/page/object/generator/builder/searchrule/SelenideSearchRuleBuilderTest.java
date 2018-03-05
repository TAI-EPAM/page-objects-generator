package com.epam.page.object.generator.builder.searchrule;

import com.codeborne.selenide.SelenideElement;
import com.epam.page.object.generator.container.SupportedTypesContainer;
import com.epam.page.object.generator.model.ClassAndAnnotationPair;
import com.epam.page.object.generator.model.RawSearchRule;
import com.epam.page.object.generator.model.Selector;
import com.epam.page.object.generator.model.searchrule.SelenideSearchRule;
import com.epam.page.object.generator.model.searchrule.SearchRule;
import com.epam.page.object.generator.util.SearchRuleExtractor;
import com.epam.page.object.generator.util.SearchRuleType;
import com.epam.page.object.generator.util.SelectorUtils;
import com.epam.page.object.generator.util.XpathToCssTransformer;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.support.FindBy;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class SelenideSearchRuleBuilderTest {

    @Mock
    private RawSearchRule rawSearchRule;

    private SelenideSearchRuleBuilder sut = new SelenideSearchRuleBuilder();
    private SupportedTypesContainer container = new SupportedTypesContainer();
    private XpathToCssTransformer transformer = new XpathToCssTransformer();
    private SelectorUtils selectorUtils = new SelectorUtils();
    private SearchRuleExtractor searchRuleExtractor = new SearchRuleExtractor();

    private SelenideSearchRule expectedSearchRule = new SelenideSearchRule("text",
            SearchRuleType.SELENIDE_ELEMENT, new Selector("css", ".myClass"),
            new ClassAndAnnotationPair(SelenideElement.class, FindBy.class), transformer, selectorUtils,
            true);

    @Test
    public void build_SelenideSearchRule_Valid() {
        MockitoAnnotations.initMocks(this);

        when(rawSearchRule.getValue(anyString())).thenReturn(expectedSearchRule.getUniqueness());
        when(rawSearchRule.getType()).thenReturn(expectedSearchRule.getType());
        when(rawSearchRule.getSelector()).thenReturn(expectedSearchRule.getSelector());

        SearchRule searchRule = sut.buildSearchRule(rawSearchRule, container, transformer,
                selectorUtils, searchRuleExtractor);
        SelenideSearchRule actualSearchRule = (SelenideSearchRule) searchRule;

        assertEquals(expectedSearchRule.getUniqueness(), actualSearchRule.getUniqueness());
        assertEquals(expectedSearchRule.getType(), actualSearchRule.getType());
        assertEquals(expectedSearchRule.getSelector(), actualSearchRule.getSelector());
        assertEquals(expectedSearchRule.getClassAndAnnotation().getElementClass(),
                actualSearchRule.getClassAndAnnotation().getElementClass());
        assertEquals(expectedSearchRule.getClassAndAnnotation().getElementAnnotation(),
                actualSearchRule.getClassAndAnnotation().getElementAnnotation());
    }
}