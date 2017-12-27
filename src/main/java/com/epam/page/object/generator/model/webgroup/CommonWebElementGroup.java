package com.epam.page.object.generator.model.webgroup;

import com.epam.jdi.uitests.web.selenium.elements.common.Button;
import com.epam.jdi.uitests.web.selenium.elements.common.Label;
import com.epam.page.object.generator.adapter.AnnotationMember;
import com.epam.page.object.generator.adapter.JavaAnnotation;
import com.epam.page.object.generator.adapter.JavaField;
import com.epam.page.object.generator.builder.WebElementGroupFieldBuilder;
import com.epam.page.object.generator.model.Selector;
import com.epam.page.object.generator.model.searchrule.CommonSearchRule;
import com.epam.page.object.generator.model.searchrule.SearchRule;
import com.epam.page.object.generator.model.webelement.CommonWebElement;
import com.epam.page.object.generator.model.webelement.WebElement;
import com.epam.page.object.generator.util.SelectorUtils;
import com.epam.page.object.generator.validator.ValidationResult;
import com.epam.page.object.generator.validator.ValidatorVisitor;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents group of {@link CommonWebElement} of a page and {@link SearchRule} by which this
 * elements were found. Common web elements are just simplest elements of a page, for example:
 * {@link Button} or {@link Label}.
 */
public class CommonWebElementGroup implements WebElementGroup {

    private CommonSearchRule searchRule;
    private List<WebElement> webElements;
    private SelectorUtils selectorUtils;

    private List<ValidationResult> validationResults = new ArrayList<>();

    private final static Logger logger = LoggerFactory.getLogger(CommonWebElementGroup.class);

    public CommonWebElementGroup(CommonSearchRule searchRule, List<WebElement> webElements,
                                 SelectorUtils selectorUtils) {
        this.searchRule = searchRule;
        this.webElements = webElements;
        this.selectorUtils = selectorUtils;
    }

    @Override
    public CommonSearchRule getSearchRule() {
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

    /**
     * Returns {@link JavaAnnotation} that represents {@link CommonWebElement} in generated class.
     *
     * @param annotationClass {@link JavaAnnotation} instance that is suitable for {@link
     * WebElement}
     * @param webElement {@link WebElement} that have to be represented in generated java class.
     * @return {@link JavaAnnotation} that represents {@link CommonWebElement} in generated class.
     */
    public JavaAnnotation getAnnotation(Class<?> annotationClass, WebElement webElement) {
        List<AnnotationMember> annotationMembers = new ArrayList<>();

        String uniquenessValue = webElement.getUniquenessValue();
        Selector selector = searchRule.getTransformedSelector();
        String annotationValue = getAnnotationValue(selector, uniquenessValue,
            searchRule.getUniqueness());

        annotationMembers.add(new AnnotationMember(selector.getType(), "$S", annotationValue));

        return new JavaAnnotation(annotationClass, annotationMembers);
    }

    /**
     * Returns string representation of annotation for class that will be generated.
     *
     * @param uniquenessValue value of 'uniqueness' attribute
     * @param uniqueness name of the 'uniqueness' attribute
     * @return string representation of annotation for class that will be generated
     * @throws IllegalArgumentException if selector type is unknown
     */
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
