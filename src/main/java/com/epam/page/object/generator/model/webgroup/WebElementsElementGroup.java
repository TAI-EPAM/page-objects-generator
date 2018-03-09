package com.epam.page.object.generator.model.webgroup;

import com.epam.page.object.generator.adapter.AnnotationMember;
import com.epam.page.object.generator.adapter.JavaAnnotation;
import com.epam.page.object.generator.adapter.JavaClass;
import com.epam.page.object.generator.adapter.JavaField;
import com.epam.page.object.generator.builder.WebElementGroupFieldBuilder;
import com.epam.page.object.generator.model.Selector;
import com.epam.page.object.generator.model.WebPage;
import com.epam.page.object.generator.model.searchrule.CommonSearchRule;
import com.epam.page.object.generator.model.searchrule.WebElementsSearchRule;
import com.epam.page.object.generator.model.webelement.CommonWebElement;
import com.epam.page.object.generator.model.webelement.WebElement;
import com.epam.page.object.generator.util.SelectorUtils;
import com.epam.page.object.generator.validator.ValidationResult;
import com.epam.page.object.generator.validator.ValidatorVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents {@link WebElementsSearchRule} and list of {@link CommonWebElement} which was found by
 * this rule from certain {@link WebPage}.
 */
public class WebElementsElementGroup implements WebElementGroup {

    private WebElementsSearchRule searchRule;
    private List<WebElement> webElements;

    private List<ValidationResult> validationResults = new ArrayList<>();

    private final static Logger logger = LoggerFactory.getLogger(WebElementsElementGroup.class);

    public WebElementsElementGroup(WebElementsSearchRule searchRule, List<WebElement> webElements) {
        this.searchRule = searchRule;
        this.webElements = webElements;
    }

    @Override
    public WebElementsSearchRule getSearchRule() {
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
     * @param annotationClass annotation class which used for generation annotation.
     * JavaClass}.
     * @return {@link JavaAnnotation} that represents {@link CommonWebElement} in generated class.
     */
    public JavaAnnotation getAnnotation(Class<?> annotationClass) {
        List<AnnotationMember> annotationMembers = new ArrayList<>();

        Selector selector = getSearchRule().getSelector();

        String annotationValue = getAnnotationValue(selector);

        annotationMembers.add(new AnnotationMember(selector.getType(), "$S", annotationValue));

        return new JavaAnnotation(annotationClass, annotationMembers);
    }

    /**
     * Returns string representation of annotation for class that will be generated.
     *
     * @param selector {@link Selector} from specified {@link CommonSearchRule}
     * @return string representation of annotation for class that will be generated
     * @throws IllegalArgumentException if selector type is unknown
     */
    private String getAnnotationValue(Selector selector) {
        if (selector.isCss()) {
            return selector.getValue();
        }
        throw new IllegalArgumentException("Selector type is unknown " + selector.toString());
    }
}
