package com.epam.page.object.generator.databuilder.searchrule;

import com.epam.page.object.generator.container.SupportedTypesContainer;
import com.epam.page.object.generator.databuilder.RawSearchRuleTestDataBuilder;
import com.epam.page.object.generator.model.RawSearchRule;
import com.epam.page.object.generator.model.searchrule.SelenideSearchRule;
import com.epam.page.object.generator.builder.searchrule.SelenideSearchRuleBuilder;
import com.epam.page.object.generator.model.searchrule.SearchRule;
import com.epam.page.object.generator.util.SearchRuleExtractor;
import com.epam.page.object.generator.util.SelectorUtils;
import com.epam.page.object.generator.util.XpathToCssTransformer;

public class SelenideSearchRuleTestDataBuilder {

    private static SelenideSearchRuleBuilder builder = new SelenideSearchRuleBuilder();
    private static SupportedTypesContainer typesContainer = new SupportedTypesContainer();
    private static XpathToCssTransformer transformer = new XpathToCssTransformer();
    private static SelectorUtils selectorUtils = new SelectorUtils();
    private static SearchRuleExtractor searchRuleExtractor = new SearchRuleExtractor();

    public static SelenideSearchRule getSelenideSearchRule_UniquenessValue_SelectorXpath() {
        RawSearchRule rawSearchRule = RawSearchRuleTestDataBuilder
            .getSelenideSearchRule_UniquenessValue_SelectorXpath();

        SearchRule searchRule = builder
            .buildSearchRule(rawSearchRule, typesContainer, transformer, selectorUtils,
                searchRuleExtractor);

        return (SelenideSearchRule) searchRule;
    }

    public static SelenideSearchRule getSelenideSearchRule_UniquenessText_SelectorXpath() {
        RawSearchRule rawSearchRule = RawSearchRuleTestDataBuilder
            .getSelenideSearchRule_UniquenessText_SelectorXpath();

        SearchRule searchRule = builder
            .buildSearchRule(rawSearchRule, typesContainer, transformer, selectorUtils,
                searchRuleExtractor);

        return (SelenideSearchRule) searchRule;
    }

    public static SelenideSearchRule getSelenideSearchRule_UniquenessValue_SelectorCss() {
        RawSearchRule rawSearchRule = RawSearchRuleTestDataBuilder
            .getSelenideSearchRule_UniquenessValue_SelectorCss();

        SearchRule searchRule = builder
            .buildSearchRule(rawSearchRule, typesContainer, transformer, selectorUtils,
                searchRuleExtractor);

        return (SelenideSearchRule) searchRule;
    }

    public static SelenideSearchRule getSelenideSearchRule_UniquenessText_SelectorCss() {
        RawSearchRule rawSearchRule = RawSearchRuleTestDataBuilder
            .getSelenideSearchRule_UniquenessText_SelectorCss();

        SearchRule searchRule = builder
            .buildSearchRule(rawSearchRule, typesContainer, transformer, selectorUtils,
                searchRuleExtractor);

        return (SelenideSearchRule) searchRule;
    }
}
