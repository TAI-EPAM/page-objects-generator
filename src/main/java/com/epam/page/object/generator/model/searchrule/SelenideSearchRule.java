package com.epam.page.object.generator.model.searchrule;

import com.codeborne.selenide.ElementsCollection;
import com.epam.page.object.generator.error.XpathToCssTransformerException;
import com.epam.page.object.generator.model.ClassAndAnnotationPair;
import com.epam.page.object.generator.model.Selector;
import com.epam.page.object.generator.model.webelement.CommonWebElement;
import com.epam.page.object.generator.model.webelement.SelenideWebElement;
import com.epam.page.object.generator.model.webelement.WebElement;
import com.epam.page.object.generator.model.webgroup.CommonWebElementGroup;
import com.epam.page.object.generator.model.webgroup.SelenideWebElementGroup;
import com.epam.page.object.generator.model.webgroup.WebElementGroup;
import com.epam.page.object.generator.util.SearchRuleType;
import com.epam.page.object.generator.util.SelectorUtils;
import com.epam.page.object.generator.util.XpathToCssTransformer;
import com.epam.page.object.generator.validator.ValidationResult;
import com.epam.page.object.generator.validator.ValidatorVisitor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link SelenideSearchRule} describes {@link SearchRule} with one of the type defined in property
 * file in 'commonSearchRule' group.
 */
public class SelenideSearchRule implements SearchRule {

    private String uniqueness;
    private SearchRuleType type;
    private Selector selector;
    private ClassAndAnnotationPair classAndAnnotation;
    private XpathToCssTransformer transformer;
    private SelectorUtils selectorUtils;
    private boolean annotation;

    private List<ValidationResult> validationResults = new ArrayList<>();
    private final static Logger logger = LoggerFactory.getLogger(SelenideSearchRule.class);

    public SelenideSearchRule(String uniqueness, SearchRuleType type, Selector selector,
                              ClassAndAnnotationPair classAndAnnotation,
                              XpathToCssTransformer transformer,
                              SelectorUtils selectorUtils,
                              boolean annotation) {
        this.uniqueness = uniqueness;
        this.type = type;
        this.selector = selector;
        this.annotation = annotation;
        this.classAndAnnotation = classAndAnnotation;
        this.transformer = transformer;
        this.selectorUtils = selectorUtils;
    }

    public String getUniqueness() {
        return uniqueness;
    }

    public SearchRuleType getType() {
        return type;
    }

    private String getRequiredValue(Element element) {
        return uniqueness.equals("text")
            ? element.text()
            : element.attr(uniqueness);
    }

    public ClassAndAnnotationPair getClassAndAnnotation() {
        return classAndAnnotation;
    }

    /**
     * If 'uniqueness' attribute not equals "text" and selector of xpath type, then we could
     * transform it to css.
     *
     * Otherwise return selector without transformation.
     *
     * @return transformed {@link Selector}
     */
    public Selector getTransformedSelector() {
        logger.debug("Transforming selector {}", selector);
        if (!uniqueness.equalsIgnoreCase("text") && selector.isXpath()) {
            try {
                return transformer.getCssSelector(selector);
            } catch (XpathToCssTransformerException e) {
                logger.error("Failed transforming selector = {}", selector, e);
            }
        }
        logger.debug("Don't need to transform selector");
        return selector;
    }

    public boolean usesAnnotation() {
        return annotation;
    }

    @Override
    public Selector getSelector() {

        return selector;
    }

    @Override
    public List<WebElement> getWebElements(Elements elements) {
        List<WebElement> webElements = new ArrayList<>();
        for (Element element : elements) {
            webElements.add(new SelenideWebElement(element, getRequiredValue(element)));
        }
        return webElements;
    }

    @Override
    public void fillWebElementGroup(List<WebElementGroup> webElementGroups, Elements elements) {
        webElementGroups.add(new SelenideWebElementGroup(this, getWebElements(elements),
            selectorUtils));
    }

    @Override
    public void accept(ValidatorVisitor validatorVisitor) {
        ValidationResult visit = validatorVisitor.visit(this);
        logger.debug("{} is {}, reason {}", this, visit.isValid(), visit.getReason());
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
            "uniqueness='" + uniqueness + '\'' +
            ", type='" + type + '\'' +
            ", selector=" + selector +
            '}';
    }
}
