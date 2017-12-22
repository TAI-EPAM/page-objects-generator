package com.epam.page.object.generator;

import com.epam.page.object.generator.adapter.JavaFileWriter;
import com.epam.page.object.generator.builder.JavaClassBuilder;
import com.epam.page.object.generator.builder.WebElementGroupFieldBuilder;
import com.epam.page.object.generator.builder.webpage.LocalWebPageBuilder;
import com.epam.page.object.generator.builder.webpage.UrlWebPageBuilder;
import com.epam.page.object.generator.builder.searchrule.SearchRuleBuildersFactory;
import com.epam.page.object.generator.builder.webpage.WebPageBuilder;
import com.epam.page.object.generator.container.SupportedTypesContainer;
import com.epam.page.object.generator.util.PropertyLoader;
import com.epam.page.object.generator.util.RawSearchRuleMapper;
import com.epam.page.object.generator.util.SearchRuleExtractor;
import com.epam.page.object.generator.util.SearchRuleGroupsScheme;
import com.epam.page.object.generator.util.SelectorUtils;
import com.epam.page.object.generator.util.TypeTransformer;
import com.epam.page.object.generator.util.ValidationChecker;
import com.epam.page.object.generator.util.XpathToCssTransformer;
import com.epam.page.object.generator.util.SearchRuleGroups;
import com.epam.page.object.generator.validator.JsonSchemaValidator;
import com.epam.page.object.generator.validator.JsonValidators;
import com.epam.page.object.generator.validator.ValidationExceptionConverter;
import com.epam.page.object.generator.validator.WebValidators;

public class PageObjectGeneratorFactory {

    public static PageObjectsGenerator getPageObjectGenerator(String packageName,
                                                              String propertyFile,
                                                              boolean onlineVersion) {
        PropertyLoader propertyLoader = new PropertyLoader(propertyFile);
        SearchRuleGroupsScheme searchRuleGroupsScheme = propertyLoader.getMapWithScheme();
        SearchRuleGroups searchRuleGroupList = propertyLoader.getSearchRuleGroupList();
        RawSearchRuleMapper rawSearchRuleMapper = new RawSearchRuleMapper(searchRuleGroupsScheme,
            searchRuleGroupList);
        JsonSchemaValidator validator = new JsonSchemaValidator(new ValidationExceptionConverter());

        SearchRuleBuildersFactory searchRuleBuildersFactory = new SearchRuleBuildersFactory(
            rawSearchRuleMapper);
        XpathToCssTransformer xpathToCssTransformer = new XpathToCssTransformer();
        TypeTransformer transformer = new TypeTransformer(new SupportedTypesContainer(),
            xpathToCssTransformer, searchRuleBuildersFactory.getMapWithBuilders());
        ValidationChecker checker = new ValidationChecker();
        JavaFileWriter javaPoetAdapter = new JavaFileWriter();
        JsonValidators jsonValidators = new JsonValidators();
        WebValidators webValidators = new WebValidators();
        JavaClassBuilder javaClassBuilder = new JavaClassBuilder(packageName);
        WebElementGroupFieldBuilder webElementGroupFieldBuilder = new WebElementGroupFieldBuilder();
        SelectorUtils selectorUtils = new SelectorUtils();
        SearchRuleExtractor searchRuleExtractor = new SearchRuleExtractor();

        WebPageBuilder builder;
        if (onlineVersion) {
            builder = new UrlWebPageBuilder();
        } else {
            builder = new LocalWebPageBuilder();
        }

        return new PageObjectsGenerator(rawSearchRuleMapper, validator, transformer, checker,
            jsonValidators, webValidators, javaClassBuilder, webElementGroupFieldBuilder,
            javaPoetAdapter, builder, selectorUtils, searchRuleExtractor);
    }
}