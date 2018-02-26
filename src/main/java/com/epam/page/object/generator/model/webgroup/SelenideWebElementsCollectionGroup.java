package com.epam.page.object.generator.model.webgroup;

import com.epam.page.object.generator.adapter.AnnotationMember;
import com.epam.page.object.generator.adapter.JavaAnnotation;
import com.epam.page.object.generator.adapter.JavaField;
import com.epam.page.object.generator.builder.WebElementGroupFieldBuilder;
import com.epam.page.object.generator.model.Selector;
import com.epam.page.object.generator.model.WebPage;
import com.epam.page.object.generator.model.searchrule.ComplexInnerSearchRule;
import com.epam.page.object.generator.model.searchrule.SelenideElementsCollectionSearchRule;
import com.epam.page.object.generator.model.webelement.ComplexWebElement;
import com.epam.page.object.generator.model.webelement.WebElement;
import com.epam.page.object.generator.util.SelectorUtils;
import com.epam.page.object.generator.validator.ValidationResult;
import com.epam.page.object.generator.validator.ValidatorVisitor;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents {@link SelenideWebElementsCollectionGroup} and list of {@link ComplexWebElement} which was found by
 * this rule from certain {@link WebPage}.
 */
public class SelenideWebElementsCollectionGroup implements WebElementGroup {

    private SelenideElementsCollectionSearchRule searchRule;
    private List<WebElement> webElements;
    private SelectorUtils selectorUtils;

    private List<ValidationResult> validationResults = new ArrayList<>();

    private final static Logger logger = LoggerFactory.getLogger(ComplexWebElementGroup.class);

    public SelenideWebElementsCollectionGroup(SelenideElementsCollectionSearchRule searchRule, List<WebElement> webElements,
                                              SelectorUtils selectorUtils) {
        this.searchRule = searchRule;
        this.webElements = webElements;
        this.selectorUtils = selectorUtils;
    }

    @Override
    public SelenideElementsCollectionSearchRule getSearchRule() {
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

    public JavaAnnotation getAnnotation(Class<?> annotationClass, WebElement webElement) { //TODO: getUniquenessValue()

        List<AnnotationMember> annotationMembers = new ArrayList<>();
        StringBuilder foo = new StringBuilder();
        String selectorType = "css";

        for (ComplexInnerSearchRule innerSearchRule : searchRule.getInnerSearchRules()) {

            String annotationElementName = innerSearchRule.getTitle();
            Selector selector;
            if (annotationElementName.equals("root")) {
                selector = innerSearchRule.getTransformedSelector();
                selectorType = selector.getType();
            } else {
                selector = innerSearchRule.getSelector();
            }

            foo.append(selector.getValue()).append(" ");
        }

        annotationMembers
                .add(new AnnotationMember(selectorType, "$S",
                        foo.toString().trim()));

        return new JavaAnnotation(annotationClass, annotationMembers);
    }
}
