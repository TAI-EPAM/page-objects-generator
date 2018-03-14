package com.epam.page.object.generator.databuilder.searchrule;

import com.epam.page.object.generator.builder.searchrule.SelenideElementsCollectionSearchRuleBuilder;
import com.epam.page.object.generator.container.SupportedTypesContainer;
import com.epam.page.object.generator.databuilder.RawSearchRuleTestDataBuilder;
import com.epam.page.object.generator.model.RawSearchRule;
import com.epam.page.object.generator.model.searchrule.SearchRule;
import com.epam.page.object.generator.model.searchrule.SelenideElementsCollectionSearchRule;
import com.epam.page.object.generator.util.SearchRuleExtractor;
import com.epam.page.object.generator.util.SelectorUtils;
import com.epam.page.object.generator.util.XpathToCssTransformer;

public class SelenideElementsCollectionSearchRuleTestDataBuilder {

    private static SelenideElementsCollectionSearchRuleBuilder builder =
            new SelenideElementsCollectionSearchRuleBuilder();
    private static SupportedTypesContainer typesContainer = new SupportedTypesContainer();
    private static XpathToCssTransformer transformer = new XpathToCssTransformer();
    private static SelectorUtils selectorUtils = new SelectorUtils();
    private static SearchRuleExtractor searchRuleExtractor = new SearchRuleExtractor();

    public static SelenideElementsCollectionSearchRule getSelenideSearchRule_UniquenessValue_SelectorCss() {
        RawSearchRule rawSearchRule = RawSearchRuleTestDataBuilder
            .getSelenideElementsCollectionSearchRule_UniquenessValue_SelectorCss();

        SearchRule searchRule = builder
            .buildSearchRule(rawSearchRule, typesContainer, transformer, selectorUtils,
                searchRuleExtractor);

        return (SelenideElementsCollectionSearchRule) searchRule;
    }

    public static SelenideElementsCollectionSearchRule getSelenideSearchRule_UniquenessText_SelectorCss() {
        RawSearchRule rawSearchRule = RawSearchRuleTestDataBuilder
            .getSelenideElementsCollectionSearchRule_UniquenessText_SelectorCss();

        SearchRule searchRule = builder
            .buildSearchRule(rawSearchRule, typesContainer, transformer, selectorUtils,
                searchRuleExtractor);

        return (SelenideElementsCollectionSearchRule) searchRule;
    }
}
