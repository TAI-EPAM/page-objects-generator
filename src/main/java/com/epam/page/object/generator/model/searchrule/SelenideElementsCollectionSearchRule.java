package com.epam.page.object.generator.model.searchrule;

import com.epam.page.object.generator.model.ClassAndAnnotationPair;
import com.epam.page.object.generator.model.Selector;
import com.epam.page.object.generator.model.webelement.SelenideElementsCollectionWebElement;
import com.epam.page.object.generator.model.webgroup.SelenideWebElementsCollectionGroup;
import com.epam.page.object.generator.model.webgroup.WebElementGroup;
import com.epam.page.object.generator.model.webelement.WebElement;
import com.epam.page.object.generator.util.SearchRuleType;
import com.epam.page.object.generator.util.SelectorUtils;
import com.epam.page.object.generator.validator.ValidationResult;
import com.epam.page.object.generator.validator.ValidatorVisitor;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link SelenideElementsCollectionSearchRule} describes {@link SearchRule} with one of the type defined in
 * property file in 'complexSearchRule' group.
 */
public class SelenideElementsCollectionSearchRule implements SearchRule {

    private SearchRuleType type;
    private List<ComplexInnerSearchRule> complexInnerSearchRules;
    private ClassAndAnnotationPair classAndAnnotation;
    private SelectorUtils selectorUtils;
    private boolean annotation;

    private List<ValidationResult> validationResults = new ArrayList<>();
    private final static Logger logger = LoggerFactory.getLogger(ComplexSearchRule.class);

    public SelenideElementsCollectionSearchRule(SearchRuleType type,
                             List<ComplexInnerSearchRule> complexInnerSearchRules,
                             ClassAndAnnotationPair classAndAnnotation,
                             SelectorUtils selectorUtils,
                                                boolean annotation) {
        this.type = type;
        this.complexInnerSearchRules = complexInnerSearchRules;
        this.annotation = annotation;
        this.classAndAnnotation = classAndAnnotation;
        this.selectorUtils = selectorUtils;
    }

    public SearchRuleType getType() {
        return type;
    }

    public boolean usesAnnotation() {
        return annotation;
    }

    public List<ComplexInnerSearchRule> getInnerSearchRules() {
        return complexInnerSearchRules;
    }

    public ComplexInnerSearchRule getRoot() {
        return complexInnerSearchRules.stream()
                .filter(innerSearchRule -> innerSearchRule.getTitle().equals("root"))
                .findFirst().orElse(null);
    }

    private String getRequiredValue(Element element) {
        String uniqueness = getRoot().getUniqueness();
        return uniqueness.equals("text")
                ? element.text()
                : element.attr(uniqueness);
    }

    public ClassAndAnnotationPair getClassAndAnnotation() {
        return classAndAnnotation;
    }

    public String getUniqueness() {
        return getRoot().getUniqueness();
    }

    @Override
    public void accept(ValidatorVisitor validatorVisitor) {
        ValidationResult visit = validatorVisitor.visit(this);
        logger.debug(this + " is '" + visit.isValid() + "', reason '" + visit.getReason() + "'");
        validationResults.add(visit);
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
        return "SearchRule{" +
                "type='" + type + '\'' +
                ", complexInnerSearchRules=" + complexInnerSearchRules +
                '}';
    }

    @Override
    public Selector getSelector() {
        return getRoot().getSelector();
    }

    @Override
    public List<WebElement> getWebElements(Elements elements) {
        List<WebElement> webElements = new ArrayList<>();
        for (Element element : elements) {
            webElements.add(new SelenideElementsCollectionWebElement(element, getRequiredValue(element), getRoot()));
        }
        return webElements;
    }

    @Override
    public void fillWebElementGroup(List<WebElementGroup> webElementGroups, Elements elements) {
        webElementGroups.add(new SelenideWebElementsCollectionGroup(this, getWebElements(elements),
                selectorUtils));
    }
}
