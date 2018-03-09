package com.epam.page.object.generator.builder.searchrule;

import com.epam.page.object.generator.container.SupportedTypesContainer;
import com.epam.page.object.generator.model.ClassAndAnnotationPair;
import com.epam.page.object.generator.model.RawSearchRule;
import com.epam.page.object.generator.model.Selector;
import com.epam.page.object.generator.model.searchrule.SearchRule;
import com.epam.page.object.generator.model.searchrule.WebElementsSearchRule;
import com.epam.page.object.generator.util.SearchRuleExtractor;
import com.epam.page.object.generator.util.SearchRuleType;
import com.epam.page.object.generator.util.SelectorUtils;
import com.epam.page.object.generator.util.XpathToCssTransformer;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.support.FindBy;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import static org.hamcrest.CoreMatchers.equalTo;

public class ListWebElementSearchRuleBuilderTest {

    private WebElementsSearchRuleBuilder webElementsSearchRuleBuilder = new WebElementsSearchRuleBuilder();
    private SupportedTypesContainer container = new SupportedTypesContainer();
    private XpathToCssTransformer transformer = new XpathToCssTransformer();
    private SelectorUtils selectorUtils = new SelectorUtils();
    private SearchRuleExtractor searchRuleExtractor = new SearchRuleExtractor();

    private WebElementsSearchRule expectedSearchRule = new WebElementsSearchRule(
            "webelements",
            SearchRuleType.WEBELEMENTS,
            new Selector("css", ".myClass"),
            new ClassAndAnnotationPair(null, FindBy.class),
            transformer,
            selectorUtils);

    @Test
    public void build_ListWebElementSearchRule_Valid() {
        RawSearchRule rawSearchRule = Mockito.mock(RawSearchRule.class);

        when(rawSearchRule.getType()).thenReturn(expectedSearchRule.getType());
        when(rawSearchRule.getSelector()).thenReturn(expectedSearchRule.getSelector());

        SearchRule searchRule = webElementsSearchRuleBuilder.buildSearchRule(rawSearchRule, container, transformer,
                selectorUtils, searchRuleExtractor);

        WebElementsSearchRule actualSearchRule = (WebElementsSearchRule) searchRule;

        assertThat(actualSearchRule.getType(), equalTo(expectedSearchRule.getType()));
        assertThat(actualSearchRule.getSelector(), equalTo(expectedSearchRule.getSelector()));
        assertThat(actualSearchRule.getClassAndAnnotation().getElementClass(),
                equalTo(expectedSearchRule.getClassAndAnnotation().getElementClass()));
        assertThat(actualSearchRule.getClassAndAnnotation().getElementAnnotation(),
                equalTo(expectedSearchRule.getClassAndAnnotation().getElementAnnotation()));
    }
}
