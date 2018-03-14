package com.epam.page.object.generator.model.searchrule;

import com.epam.page.object.generator.model.ClassAndAnnotationPair;
import com.epam.page.object.generator.model.Selector;
import com.epam.page.object.generator.model.webelement.ListWebElement;
import com.epam.page.object.generator.model.webelement.WebElement;
import com.epam.page.object.generator.model.webgroup.WebElementGroup;
import com.epam.page.object.generator.model.webgroup.WebElementsElementGroup;
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
 * {@link WebElementsSearchRule} describes {@link SearchRule} with one of the type defined in property
 * file in 'commonSearchRule' group.
 */
public class WebElementsSearchRule implements SearchRule {

    private SearchRuleType type;
    private Selector selector;
    private ClassAndAnnotationPair classAndAnnotation;
    private XpathToCssTransformer transformer;
    private SelectorUtils selectorUtils;
    private String fieldName;

    private List<ValidationResult> validationResults = new ArrayList<>();
    private final static Logger logger = LoggerFactory.getLogger(WebElementsSearchRule.class);

    public WebElementsSearchRule(String fieldName, SearchRuleType type, Selector selector,
                                 ClassAndAnnotationPair classAndAnnotation,
                                 XpathToCssTransformer transformer,
                                 SelectorUtils selectorUtils) {
        this.fieldName = fieldName;
        this.type = type;
        this.selector = selector;

        this.classAndAnnotation = classAndAnnotation;
        this.transformer = transformer;
        this.selectorUtils = selectorUtils;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public SearchRuleType getType() {
        return type;
    }

    public ClassAndAnnotationPair getClassAndAnnotation() {
        return classAndAnnotation;
    }

    @Override
    public Selector getSelector() {
        return selector;
    }

    @Override
    public List<WebElement> getWebElements(Elements elements) {
        List<WebElement> webElements = new ArrayList<>();
        for (Element element : elements) {
            webElements.add(new ListWebElement(element));
        }
        return webElements;
    }

    @Override
    public void fillWebElementGroup(List<WebElementGroup> webElementGroups, Elements elements) {
        webElementGroups.add(new WebElementsElementGroup(this, getWebElements(elements)));
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
            ", type='" + type + '\'' +
            ", selector=" + selector +
            '}';
    }
}
