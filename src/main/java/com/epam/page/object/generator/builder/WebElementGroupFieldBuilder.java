package com.epam.page.object.generator.builder;

import static com.epam.page.object.generator.util.StringUtils.splitCamelCase;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import com.epam.page.object.generator.adapter.JavaField;
import com.epam.page.object.generator.adapter.JavaAnnotation;
import com.epam.page.object.generator.model.Selector;
import com.epam.page.object.generator.model.searchrule.*;
import com.epam.page.object.generator.model.webgroup.*;
import com.epam.page.object.generator.model.searchrule.CommonSearchRule;
import com.epam.page.object.generator.model.searchrule.ComplexSearchRule;
import com.epam.page.object.generator.model.searchrule.FormSearchRule;
import com.epam.page.object.generator.model.searchrule.WebElementsSearchRule;
import com.epam.page.object.generator.model.webelement.ListWebElement;
import com.epam.page.object.generator.model.webgroup.CommonWebElementGroup;
import com.epam.page.object.generator.model.webgroup.ComplexWebElementGroup;
import com.epam.page.object.generator.model.webgroup.FormWebElementGroup;
import com.epam.page.object.generator.model.webelement.WebElement;

import java.util.*;
import javax.lang.model.element.Modifier;

import com.epam.page.object.generator.model.webgroup.WebElementsElementGroup;
import com.squareup.javapoet.TypeName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebElementGroupFieldBuilder knows how to build list of {@link JavaField} from the {@link
 * WebElementGroup}.<br/>
 *
 * This class based on Visitor pattern. If you want to add a new group of elements, you need to
 * create a new class which will implement {@link WebElementGroup} interface and than create new
 * build(NewWebElementGroup newWebElementGroup) method in WebElementGroupFieldBuilder which will
 * take your NewWebElementGroup as a parameter.
 */
public class WebElementGroupFieldBuilder {

    private static final Logger logger = LoggerFactory.getLogger(WebElementGroupFieldBuilder.class);

    private Set<String> allowedSuffixes = new HashSet<>();
    private boolean addElementSuffix;

    public List<JavaField> build(CommonWebElementGroup commonWebElementGroup) {
        List<JavaField> javaFields = new ArrayList<>();
        CommonSearchRule searchRule = commonWebElementGroup.getSearchRule();

        logger.debug("Add fields found by " + searchRule);
        for (WebElement webElement : commonWebElementGroup.getWebElements()) {
            Class<?> elementClass = searchRule.getClassAndAnnotation().getElementClass();
            String className = elementClass.getName();
            String fieldName = extractFieldName(webElement, elementClass.getSimpleName());
            JavaField javaField;
            Modifier[] modifiers = new Modifier[]{PUBLIC};
            Class<?> annotationClass = searchRule.getClassAndAnnotation().getElementAnnotation();
            JavaAnnotation annotation = commonWebElementGroup
                    .getAnnotation(annotationClass, webElement);

            javaField = new JavaField(className, fieldName, annotation, modifiers);
            javaField.setAnnotationFlag(true);

            if (javaField.isSelenideTypeField()) {
                Selector selector = searchRule.getTransformedSelector();
                javaField.setInitializer(selector);
            }

            javaFields.add(javaField);
            logger.debug("Add field = " + javaField);
        }
        logger.debug("Finish " + searchRule + "\n");

        return javaFields;
    }

    public List<JavaField> build(WebElementsElementGroup webElementsElementGroup) {
        List<JavaField> javaFields = new ArrayList<>();
        WebElementsSearchRule searchRule = webElementsElementGroup.getSearchRule();

        logger.debug("Add fields found by " + searchRule);

        if (!webElementsElementGroup.getWebElements().isEmpty()) {

            TypeName typeName = ListWebElement.getType();
            String fieldName = searchRule.getFieldName();

            JavaField javaField;
            Modifier[] modifiers = new Modifier[]{PUBLIC};
            Class<?> annotationClass = searchRule.getClassAndAnnotation().getElementAnnotation();
            JavaAnnotation annotation = webElementsElementGroup
                    .getAnnotation(annotationClass);

            javaField = new JavaField(typeName, fieldName, annotation, modifiers);

            javaFields.add(javaField);
            logger.debug("Add field = " + javaField);
        }
        logger.debug("Finish " + searchRule + "\n");
        return javaFields;
    }

    public List<JavaField> build(ComplexWebElementGroup complexWebElementGroup) {
        List<JavaField> javaFields = new ArrayList<>();
        ComplexSearchRule searchRule = complexWebElementGroup.getSearchRule();

        logger.debug("Add fields found by " + searchRule);
        for (WebElement webElement : complexWebElementGroup.getWebElements()) {
            Class<?> elementClass = searchRule.getClassAndAnnotation().getElementClass();
            String className = elementClass.getName();
            String fieldName = extractFieldName(webElement, elementClass.getSimpleName());
            Class<?> annotationClass = searchRule.getClassAndAnnotation().getElementAnnotation();
            JavaAnnotation annotation = complexWebElementGroup
                .getAnnotation(annotationClass, webElement);
            Modifier[] modifiers = new Modifier[]{PUBLIC};

            JavaField javaField = new JavaField(className, fieldName, annotation, modifiers);
            javaFields.add(javaField);
            logger.debug("Add field = " + javaField);
        }
        logger.debug("Finish " + searchRule + "\n");

        return javaFields;
    }

    public List<JavaField> build(FormWebElementGroup formWebElementGroup,
                                 String packageName) {
        FormSearchRule searchRule = formWebElementGroup.getSearchRule();

        logger.debug("Add field found by " + searchRule);
        String className = packageName + ".form." + capitalize(searchRule.getSection());
        String fieldName = uncapitalize(searchRule.getSection());
        Class<?> annotationClass = searchRule.getClassAndAnnotation().getElementAnnotation();
        JavaAnnotation annotation = formWebElementGroup.getAnnotation(annotationClass);
        Modifier[] modifiers = new Modifier[]{PUBLIC};

        JavaField javaField = new JavaField(className, fieldName, annotation, modifiers);
        logger.debug("Add field = " + javaField);
        logger.debug("Finish " + searchRule + "\n");
        return Collections.singletonList(javaField);
    }

    public List<JavaField> build(SelenideWebElementGroup selenideWebElementGroup) {
        List<JavaField> javaFields = new ArrayList<>();
        SelenideSearchRule searchRule = selenideWebElementGroup.getSearchRule();

        logger.debug("Add fields found by " + searchRule);
        for (WebElement webElement : selenideWebElementGroup.getWebElements()) {
            Class<?> elementClass = searchRule.getClassAndAnnotation().getElementClass();
            String className = elementClass.getName();
            String fieldName = extractFieldName(webElement, elementClass.getSimpleName());
            JavaField javaField;
            Modifier[] modifiers = new Modifier[]{PUBLIC};
            Class<?> annotationClass = searchRule.getClassAndAnnotation().getElementAnnotation();
            JavaAnnotation annotation = selenideWebElementGroup
                    .getAnnotation(annotationClass, webElement);

            javaField = new JavaField(className, fieldName, annotation, modifiers);
            javaField.setAnnotationFlag(searchRule.usesAnnotation());
            if (javaField.isSelenideTypeField()) {
                Selector selector = searchRule.getTransformedSelector();
                javaField.setInitializer(selector);
            }

            javaFields.add(javaField);
            logger.debug("Add field = " + javaField);
        }
        logger.debug("Finish " + searchRule + "\n");

        return javaFields;
    }

    public List<JavaField> build(SelenideElementsCollectionWebElementGroup selenideElementsCollectionWebElementGroup) {
        List<JavaField> javaFields = new ArrayList<>();
        SelenideElementsCollectionSearchRule searchRule = selenideElementsCollectionWebElementGroup.getSearchRule();

        logger.debug("Add fields found by " + searchRule);
        if (!selenideElementsCollectionWebElementGroup.getWebElements().isEmpty()) {
            WebElement webElement = selenideElementsCollectionWebElementGroup.getWebElements().get(0);
            Class<?> elementClass = searchRule.getClassAndAnnotation().getElementClass();
            String className = elementClass.getName();
            String fieldName = searchRule.getFieldName();
            JavaField javaField;
            Modifier[] modifiers = new Modifier[]{PUBLIC};
            Class<?> annotationClass = searchRule.getClassAndAnnotation().getElementAnnotation();
            JavaAnnotation annotation = selenideElementsCollectionWebElementGroup
                    .getAnnotation(annotationClass, webElement);

            javaField = new JavaField(className, fieldName, annotation, modifiers);
            javaField.setAnnotationFlag(searchRule.usesAnnotation());
            if (javaField.isSelenideTypeField()) {
                javaField.setElementsCollectionInitializer();
            }

            javaFields.add(javaField);
            logger.debug("Add field = " + javaField);
        }
        logger.debug("Finish " + searchRule + "\n");

        return javaFields;
    }

    private String extractFieldName(WebElement webElement, String suffix) {
        String fieldName = uncapitalize(splitCamelCase(webElement.getUniquenessValue()));

        if (addElementSuffix) {
            fieldName = addSuffix(fieldName, suffix);
        }
        return fieldName;
    }

    private String addSuffix(String fieldName, String suffix) {
        String lowerCaseSuffix = suffix.toLowerCase();

        boolean suffixAllowed = allowedSuffixes.contains(lowerCaseSuffix);

        boolean fieldNameEndsWithSuffix = fieldName.toLowerCase().endsWith(lowerCaseSuffix);
        if (suffixAllowed && !fieldNameEndsWithSuffix) {
            fieldName = fieldName + suffix;
        }
        return fieldName;
    }

    /**
     * Method allows to edit output method names by element name suffix<br/> e.g <i>public Button
     * value -> public Button valueButton</i><br/> <p>
     *
     * Works only with suffixes from file <i>resources/allowedSuffixes.properties</i>,
     * if the element doesn't end with suffix
     */
    public void setAddElementSuffix(boolean addElementSuffix) {
        this.addElementSuffix = addElementSuffix;
    }

    public void setAllowedSuffixes(Set<String> allowedSuffixes) {
        this.allowedSuffixes = allowedSuffixes;
    }
}
