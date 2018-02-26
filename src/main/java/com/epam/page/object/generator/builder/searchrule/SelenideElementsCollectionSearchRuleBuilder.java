package com.epam.page.object.generator.builder.searchrule;

import com.epam.page.object.generator.container.SupportedTypesContainer;
import com.epam.page.object.generator.model.ClassAndAnnotationPair;
import com.epam.page.object.generator.model.RawSearchRule;
import com.epam.page.object.generator.model.searchrule.ComplexInnerSearchRule;
import com.epam.page.object.generator.model.searchrule.SelenideElementsCollectionSearchRule;
import com.epam.page.object.generator.model.searchrule.SearchRule;
import com.epam.page.object.generator.util.RawSearchRuleMapper;
import com.epam.page.object.generator.util.SearchRuleExtractor;
import com.epam.page.object.generator.util.SearchRuleType;
import com.epam.page.object.generator.util.SelectorUtils;
import com.epam.page.object.generator.util.XpathToCssTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is needed for creating {@link SelenideElementsCollectionSearchRuleBuilder} from {@link RawSearchRule}
 */
public class SelenideElementsCollectionSearchRuleBuilder implements SearchRuleBuilder {


    private final static Logger logger = LoggerFactory.getLogger(ComplexSearchRuleBuilder.class);

    private RawSearchRuleMapper rawSearchRuleMapper;
    private ComplexInnerSearchRuleBuilder builder;

    public SelenideElementsCollectionSearchRuleBuilder(RawSearchRuleMapper rawSearchRuleMapper,
                                    ComplexInnerSearchRuleBuilder builder) {
        this.rawSearchRuleMapper = rawSearchRuleMapper;
        this.builder = builder;
    }

    @Override
    public SearchRule buildSearchRule(RawSearchRule rawSearchRule,
                                      SupportedTypesContainer typesContainer,
                                      XpathToCssTransformer transformer,
                                      SelectorUtils selectorUtils,
                                      SearchRuleExtractor searchRuleExtractor) {
        logger.debug("Start transforming of " + rawSearchRule);
        SearchRuleType type = rawSearchRule.getType();
        ClassAndAnnotationPair classAndAnnotation = typesContainer.getSupportedTypesMap()
                .get(type.getName());

        List<ComplexInnerSearchRule> innerSearchRules = new ArrayList<>(); //TODO: create SelenideElementsCollectionInnerSearchRule

        logger.debug("Create list of complexInnerSearchRules...");
        List<RawSearchRule> innerRawSearchRules = rawSearchRuleMapper
                .getComplexInnerRawSearchRules(rawSearchRule);

        for (RawSearchRule innerRawSearchRule : innerRawSearchRules) {
            innerSearchRules.add((ComplexInnerSearchRule) builder
                    .buildSearchRule(innerRawSearchRule, typesContainer, transformer, selectorUtils,
                            searchRuleExtractor));
        }
        logger.debug("Finish creating list of complexInnerSearchRules");

        SelenideElementsCollectionSearchRule complexSearchRule = new SelenideElementsCollectionSearchRule(type, innerSearchRules,
                classAndAnnotation, selectorUtils, rawSearchRule.getAnnotation());
        logger.debug("Create a new " + complexSearchRule + "\n");
        return complexSearchRule;
    }
}
