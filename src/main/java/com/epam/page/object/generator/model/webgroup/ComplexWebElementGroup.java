package com.epam.page.object.generator.model.webgroup;

import com.epam.page.object.generator.adapter.AnnotationMember;
import com.epam.page.object.generator.adapter.JavaAnnotation;
import com.epam.page.object.generator.adapter.JavaField;
import com.epam.page.object.generator.builder.WebElementGroupFieldBuilder;
import com.epam.page.object.generator.model.Selector;
import com.epam.page.object.generator.model.searchrule.ComplexInnerSearchRule;
import com.epam.page.object.generator.model.searchrule.ComplexSearchRule;
import com.epam.page.object.generator.model.webelement.WebElement;
import com.epam.page.object.generator.util.SelectorUtils;
import com.epam.page.object.generator.validator.ValidationResult;
import com.epam.page.object.generator.validator.ValidatorVisitor;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComplexWebElementGroup implements WebElementGroup {

    private ComplexSearchRule searchRule;
    private List<WebElement> webElements;
    private SelectorUtils selectorUtils;

    private List<ValidationResult> validationResults = new ArrayList<>();

    private final static Logger logger = LoggerFactory.getLogger(ComplexWebElementGroup.class);

    public ComplexWebElementGroup(ComplexSearchRule searchRule, List<WebElement> webElements,
                                  SelectorUtils selectorUtils) {
        this.searchRule = searchRule;
        this.webElements = webElements;
        this.selectorUtils = selectorUtils;
    }

    @Override
    public ComplexSearchRule getSearchRule() {
        return searchRule;
    }

    @Override
    public List<WebElement> getWebElements() {
        return webElements;
    }

    @Override
    public List<JavaField> accept(WebElementGroupFieldBuilder webElementGroupFieldBuilder,
                                  String packageName) {
        return webElementGroupFieldBuilder.build(this);
    }

    @Override
    public void accept(ValidatorVisitor validatorVisitor) {
        validationResults.add(validatorVisitor.visit(this));
    }

    @Override
    public List<ValidationResult> getValidationResults() {
        return validationResults;
    }

    @Override
    public boolean isValid() {
        return validationResults.stream().allMatch(ValidationResult::isValid);
    }

    @Override
    public boolean isInvalid() {
        return validationResults.stream()
            .anyMatch(validationResultNew -> !validationResultNew.isValid());
    }

    @Override
    public String toString() {
        return searchRule.toString();
    }

    public JavaAnnotation getAnnotation(Class<?> annotationClass, WebElement webElement) {
        List<AnnotationMember> annotationMembers = new ArrayList<>();

        for (ComplexInnerSearchRule innerSearchRule : searchRule.getInnerSearchRules()) {

            String annotationElementName = innerSearchRule.getTitle();
            JavaAnnotation innerSearchRuleAnnotation;

            if (annotationElementName.equals("root")) {
                Selector selector = innerSearchRule.getTransformedSelector();
                String uniquenessValue = webElement.getUniquenessValue();
                String annotationValue = getAnnotationValue(selector, uniquenessValue,
                    searchRule.getUniqueness());

                List<AnnotationMember> innerAnnotation = new ArrayList<>();
                innerAnnotation
                    .add(new AnnotationMember(selector.getType(), "$S", annotationValue));

                innerSearchRuleAnnotation = new JavaAnnotation(FindBy.class, innerAnnotation);
            } else {
                Selector selector = innerSearchRule.getSelector();

                List<AnnotationMember> innerAnnotation = new ArrayList<>();
                innerAnnotation
                    .add(new AnnotationMember(selector.getType(), "$S", selector.getValue()));

                innerSearchRuleAnnotation = new JavaAnnotation(FindBy.class, innerAnnotation);
            }

            annotationMembers
                .add(new AnnotationMember(annotationElementName, "$L",
                    innerSearchRuleAnnotation));
        }

        return new JavaAnnotation(annotationClass, annotationMembers);
    }

    private String getAnnotationValue(Selector selector, String uniquenessValue,
                                      String uniqueness) {
        if (selector.isXpath()) {
            return selectorUtils.resultXpathSelector(selector, uniquenessValue, uniqueness);
        } else if (selector.isCss()) {
            return selectorUtils.resultCssSelector(selector, uniquenessValue, uniqueness);
        }
        throw new IllegalArgumentException("Selector type is unknown " + selector.toString());
    }
}
