package com.epam.page.object.generator.models.searchRules;

import com.epam.page.object.generator.errors.XpathToCssTransformerException;
import com.epam.page.object.generator.models.ClassAndAnnotationPair;
import com.epam.page.object.generator.models.Selector;
import com.epam.page.object.generator.models.webElementGroups.CommonWebElementGroup;
import com.epam.page.object.generator.models.webElementGroups.WebElementGroup;
import com.epam.page.object.generator.models.webElements.CommonWebElement;
import com.epam.page.object.generator.models.webElements.WebElement;
import com.epam.page.object.generator.utils.SearchRuleType;
import com.epam.page.object.generator.utils.SelectorUtils;
import com.epam.page.object.generator.utils.XpathToCssTransformer;
import com.epam.page.object.generator.validators.ValidationResult;
import com.epam.page.object.generator.validators.ValidatorVisitor;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonSearchRule implements SearchRule {

    private String uniqueness;
    private SearchRuleType type;
    private Selector selector;
    private ClassAndAnnotationPair classAndAnnotation;
    private XpathToCssTransformer transformer;
    private SelectorUtils selectorUtils;

    private List<ValidationResult> validationResults = new ArrayList<>();
    private final static Logger logger = LoggerFactory.getLogger(CommonSearchRule.class);

    public CommonSearchRule(String uniqueness, SearchRuleType type, Selector selector,
                            ClassAndAnnotationPair classAndAnnotation,
                            XpathToCssTransformer transformer,
                            SelectorUtils selectorUtils) {
        this.uniqueness = uniqueness;
        this.type = type;
        this.selector = selector;
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

    public Selector getTransformedSelector() {
        if (!uniqueness.equalsIgnoreCase("text") && selector.isXpath()) {
            try {
                return transformer.getCssSelector(selector);
            } catch (XpathToCssTransformerException e) {
                e.printStackTrace();
            }
        }

        return selector;
    }

    @Override
    public Selector getSelector() {
        return selector;
    }

    @Override
    public List<WebElement> getWebElements(Elements elements) {
        List<WebElement> webElements = new ArrayList<>();
        for (Element element : elements) {
            webElements.add(new CommonWebElement(element, getRequiredValue(element)));
        }
        return webElements;
    }

    @Override
    public void fillWebElementGroup(List<WebElementGroup> webElementGroups, Elements elements) {
        webElementGroups.add(new CommonWebElementGroup(this, getWebElements(elements),
            selectorUtils));
    }

    @Override
    public void accept(ValidatorVisitor validatorVisitor) {
        ValidationResult visit = validatorVisitor.visit(this);
        logger.info(this + " is '" + visit.isValid() + "', reason '" + visit.getReason() + "'");
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