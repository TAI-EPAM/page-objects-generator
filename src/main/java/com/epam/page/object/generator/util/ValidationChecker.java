package com.epam.page.object.generator.util;

import com.epam.page.object.generator.adapter.JavaClass;
import com.epam.page.object.generator.adapter.JavaFileWriter;
import com.epam.page.object.generator.error.ValidationException;
import com.epam.page.object.generator.model.RawSearchRule;
import com.epam.page.object.generator.model.WebPage;
import com.epam.page.object.generator.model.searchrule.SearchRule;
import com.epam.page.object.generator.model.searchrule.Validatable;
import com.epam.page.object.generator.validator.ValidationResult;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checks validation status of {@link SearchRule} search rules and web pages after validation.
 */
public class ValidationChecker {

    private final static Logger logger = LoggerFactory.getLogger(ValidationChecker.class);

    /**
     * Checks if all elements of list of {@link RawSearchRule} are valid, otherwise throws
     * Validation exception.
     *
     * @param rawSearchRules list of search rules to check
     * @throws ValidationException for first found invalid raw search rule
     */
    public void checkRawSearchRules(List<RawSearchRule> rawSearchRules) {
        if (rawSearchRules.stream().anyMatch(RawSearchRule::isInvalid)) {
            StringBuilder stringBuilder = new StringBuilder(
                "\nJson file has invalid search rules:\n");

            rawSearchRules.stream()
                .filter(RawSearchRule::isInvalid)
                .forEach(
                    rawSearchRule -> stringBuilder.append("\n").append(rawSearchRule)
                        .append(" is invalid:\n").append(rawSearchRule.getExceptionMessage()));

            throw new ValidationException(stringBuilder.toString());
        }
    }

    /**
     * Checks if all {@link SearchRule} elements are valid, otherwise throws Validation exception.
     * Basically, all logic is reduced to get message with validation information (reasons of
     * invalid state etc.) and then throw Validation exception with this message
     *
     * @param searchRuleList list of {@link SearchRule} to check validation status
     * @throws ValidationException for first found invalid {@link SearchRule} in list
     */
    public void checkSearchRules(List<SearchRule> searchRuleList) {
        if (searchRuleList.stream().anyMatch(Validatable::isInvalid)) {
            String validationResultString = searchRuleList.stream()
                .filter(Validatable::isInvalid)
                .map(Validatable::getValidationResults)
                .flatMap(Collection::stream)
                .filter(ValidationResult::isInvalid)
                .map(ValidationResult::getReason)
                .collect(Collectors.joining("\n"));

            throw new ValidationException(validationResultString);
        }
    }

    //forceGenerate - пояснить (когда true, когда false)

    /**
     * Checks validation status of {@link WebPage} list elements which are used to generate
     * java-files. Throws Validation exception for first found invalid web page in list. If
     * forceGenerateFile flag is true, then java classes are generating anyway (even if {@link
     * WebPage} is invalid, otherwise generation doesn't start. ValidationException throws in both
     * cases.
     *
     * @param webPages pages to check
     * @param javaClasses list of {@link JavaClass}
     * @param outputDir path for the folder where need to generate .java source files.
     * @param javaFileWriter {@link JavaFileWriter} used to generate classes
     * @param forceGenerateFile flag used to generate class (even if it's invalid)
     * @throws IOException if unable to write files because of IO
     * @throws ValidationException if invalid web pages were found
     */
    public void checkWebPages(List<WebPage> webPages, List<JavaClass> javaClasses, String outputDir,
                              JavaFileWriter javaFileWriter, boolean forceGenerateFile)
        throws IOException {
        if (webPages.stream().anyMatch(WebPage::hasInvalidWebElementGroup)) {
            if (forceGenerateFile) {
                logger.info("Start generating JavaClasses...");
                javaFileWriter.writeFiles(outputDir, javaClasses);
                logger.info("Finish generating JavaClasses");
            }

            String validationResultString = webPages.stream()
                .filter(WebPage::hasInvalidWebElementGroup)
                .map(WebPage::getWebElementGroups)
                .flatMap(Collection::stream)
                .map(Validatable::getValidationResults)
                .flatMap(Collection::stream)
                .filter(ValidationResult::isInvalid)
                .map(ValidationResult::getReason)
                .collect(Collectors.joining("\n"));

            throw new ValidationException(validationResultString);
        }
    }
}