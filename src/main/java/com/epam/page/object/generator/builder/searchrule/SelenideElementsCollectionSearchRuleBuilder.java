package com.epam.page.object.generator.builder.searchrule;

import com.epam.page.object.generator.container.SupportedTypesContainer;
import com.epam.page.object.generator.model.ClassAndAnnotationPair;
import com.epam.page.object.generator.model.RawSearchRule;
import com.epam.page.object.generator.model.Selector;
import com.epam.page.object.generator.model.searchrule.SearchRule;
import com.epam.page.object.generator.model.searchrule.SelenideElementsCollectionSearchRule;
import com.epam.page.object.generator.util.SearchRuleExtractor;
import com.epam.page.object.generator.util.SearchRuleType;
import com.epam.page.object.generator.util.SelectorUtils;
import com.epam.page.object.generator.util.XpathToCssTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is needed for creating {@link SelenideElementsCollectionSearchRule} from {@link RawSearchRule}
 */
public class SelenideElementsCollectionSearchRuleBuilder implements SearchRuleBuilder {

    private final static Logger logger = LoggerFactory.getLogger(SelenideElementsCollectionSearchRuleBuilder.class);

    /**
     * This method builds {@link SelenideElementsCollectionSearchRule} getting the necessary information about {@link
     * RawSearchRule} such as {@link RawSearchRule#type}, {@link Selector}, uniqueness parameter.
     * Then based on {@link RawSearchRule#type} get {@link ClassAndAnnotationPair}. At last sent
     * this parameters plus {@link XpathToCssTransformer} and {@link SelectorUtils} in constructor
     * and get new {@link SelenideElementsCollectionSearchRule}
     *
     * @return {@link SelenideElementsCollectionSearchRule}
     */
    @Override
    public SearchRule buildSearchRule(RawSearchRule rawSearchRule,
                                      SupportedTypesContainer typesContainer,
                                      XpathToCssTransformer transformer,
                                      SelectorUtils selectorUtils,
                                      SearchRuleExtractor searchRuleExtractor) {
        logger.debug("Start transforming of {}", rawSearchRule);
        String uniqueness = rawSearchRule.getValue("uniqueness");
        SearchRuleType type = rawSearchRule.getType();
        Selector selector = rawSearchRule.getSelector();
        ClassAndAnnotationPair classAndAnnotation = typesContainer.getSupportedTypesMap()
                .get(type.getName());

        SelenideElementsCollectionSearchRule selenideSearchRule = new SelenideElementsCollectionSearchRule(
                rawSearchRule.getFieldName(), uniqueness, type, selector,
                classAndAnnotation, transformer,
                selectorUtils, rawSearchRule.usesAnnotation());
        logger.debug("Create a new {}", selenideSearchRule);

        return selenideSearchRule;
    }
}
