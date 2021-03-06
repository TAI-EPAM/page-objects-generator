package com.epam.page.object.generator.integration;

import com.epam.page.object.generator.PageObjectGeneratorFactory;
import com.epam.page.object.generator.PageObjectsGenerator;
import com.epam.page.object.generator.adapter.JavaFileWriter;
import com.epam.page.object.generator.error.NotValidUrlException;
import com.epam.page.object.generator.error.ValidationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

/**
 * Each test generate it's own Java objects for JDI. Probably it would be better to run each test in
 * isolation from others
 */
@Ignore
public class MainTest {

    private String outputDir = "src/test/resources/";
    private String packageName = "test";

    private PageObjectsGenerator pog;

    @Before
    public void setUp() throws IOException {
        FileUtils.deleteDirectory(new File(outputDir + packageName));
        pog = PageObjectGeneratorFactory.getPageObjectGenerator(packageName, "/groups.json", true);
    }

    @Test
    public void pageObjectsGenerator_GenerateCommonSearchRule_Success() throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/button.json", outputDir,
            Collections.singletonList("https://www.google.com"));
    }

    @Test
    public void pageObjectGenerator_GenerateCommonSearchRuleWithUniqueness_Success()
        throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/epam-buttons.json", outputDir,
            Collections.singletonList("https://www.epam.com"));
    }

    @Test
    public void pageObjectsGenerator_GenerateComplexSearchRule_Success() throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/dropdown.json", outputDir,
            Collections.singletonList("https://www.w3schools.com/howto/howto_js_dropdown.asp"));
    }

    @Test
    public void pageObjectsGenerator_GenerateComplexSearchRuleWithInnerElements_Success()
        throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/dropdown-inner-root.json", outputDir,
            Collections.singletonList("http://materializecss.com/dropdown.html"));
    }

    @Test
    public void pageObjectGenerator_GenerateFormSearchRule_Success() throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/form.json", outputDir,
            Collections.singletonList("https://www.w3schools.com/html/html_forms.asp"));
    }

    @Test(expected = ValidationException.class)
    public void pageObjectGenerator_NotSectionAttribute_Exception() throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/form-wrong-section.json", outputDir,
            Collections.singletonList("https://www.w3schools.com/html/html_forms.asp"));
    }

    @Test(expected = NotValidUrlException.class)
    public void pageObjectsGenerator_WrongUrl_Exception() throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/dropdown.json", outputDir,
            Collections.singletonList("https://www.w3schoolsd.com/howto/howto_js_dropdown.asp"));
    }

    @Test(expected = ValidationException.class)
    public void pageObjectsGenerator_WrongType_Exception() throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/dropdown-wrong-type.json", outputDir,
            Collections.singletonList("https://www.w3schools.com/howto/howto_js_dropdown.asp"));
    }

    @Test(expected = ValidationException.class)
    public void pageObjectsGenerator_ForceFileGenerate_Exception() throws Exception {
        pog.setForceGenerateFile(true);
        pog.generatePageObjects("/dropdown-wrong-type.json", outputDir,
            Collections.singletonList("https://www.w3schools.com/howto/howto_js_dropdown.asp"));
    }

    @Test
    public void pageObjectsGenerator_WrongSelector_Success() throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/dropdown-wrong-selector.json", outputDir,
            Collections.singletonList("https://www.w3schools.com/howto/howto_js_dropdown.asp"));
    }

    @Test(expected = ValidationException.class)
    public void pageObjectGenerator_NotExistenceRootTitle_Exception() throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/dropdown-without-root.json", outputDir,
            Collections.singletonList("http://materializecss.com/dropdown.html"));

    }

    @Test(expected = ValidationException.class)
    public void pageObjectGenerator_DuplicateInnerRoot_Exception() throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/dropdown-duplicate-root.json", outputDir,
            Collections.singletonList("http://materializecss.com/dropdown.html"));
    }

    @Test(expected = ValidationException.class)
    public void pageObjectGenerator_NotExistenceTitleIntoInnerSearchRule_Exception() throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/dropdown-wrong-title.json", outputDir,
            Collections.singletonList("http://materializecss.com/dropdown.html"));

    }

    @Test(expected = ValidationException.class)
    public void pageObjectGenerator_NotUniquenessAttributeIntoRoot_Exception() throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/dropdown-root-without-uniqueness.json", outputDir,
            Collections.singletonList("http://materializecss.com/dropdown.html"));

    }

    @Test(expected = ValidationException.class)
    public void pageObjectGenerator_DuplicateUniquenessAttributeIntoRule_Exception() throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/dropdown-duplicate-uniqueness.json", outputDir,
            Collections.singletonList("http://materializecss.com/dropdown.html"));
    }

    @Test
    public void pageObjectGenerator_NotExistUniquenessAttribute_Success() throws Exception {
        pog.setForceGenerateFile(true);
        pog.generatePageObjects("/epam-buttons-wrong-uniqueness.json", outputDir,
            Collections.singletonList("https://www.epam.com"));
    }

    @Test
    public void pageObjectGenerator_CssSelenideElement_Success() throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/selenide_element_css.json", outputDir,
            Collections.singletonList("https://www.w3schools.com/html/html_forms.asp"));
    }

    @Test(expected = ValidationException.class)
    public void pageObjectGenerator_CssSelenideElementNotExistUniquenessAttribute_Exception()
        throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/selenide_element_css_wrong_uniqueness.json", outputDir,
            Collections.singletonList("https://www.w3schools.com/html/html_forms.asp"));
    }

    @Test(expected = ValidationException.class)
    public void pageObjectGenerator_CssSelenideElementWithoutUniquenessAttribute_Exception()
        throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/selenide_element_css_without_uniqueness.json", outputDir,
            Collections.singletonList("https://www.w3schools.com/html/html_forms.asp"));
    }

    @Test
    public void pageObjectGenerator_CssSelenideElementWithDefaultAnnotation_Success()
        throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/selenide_element_css_default_annotation.json", outputDir,
            Collections.singletonList("https://www.w3schools.com/html/html_forms.asp"));
    }

    @Test
    public void pageObjectGenerator_CssSelenideElementWithDifferentElements_Success()
        throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/selenide_element_css_different_elements.json", outputDir,
            Collections.singletonList("https://www.w3schools.com/html/html_forms.asp"));
    }

    @Test
    public void pageObjectGenerator_XpathSelenideElement_Success() throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/selenide_element_xpath.json", outputDir,
            Collections.singletonList("https://www.w3schools.com/html/html_forms.asp"));
    }

    @Test(expected = ValidationException.class)
    public void pageObjectGenerator_XpathSelenideElementNotExistUniquenessAttribute_Exception()
        throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/selenide_element_xpath_wrong_uniqueness.json", outputDir,
            Collections.singletonList("https://www.w3schools.com/html/html_forms.asp"));
    }

    @Test(expected = ValidationException.class)
    public void pageObjectGenerator_XpathSelenideElementWithoutUniquenessAttribute_Exception()
        throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/selenide_element_xpath_without_uniqueness.json", outputDir,
            Collections.singletonList("https://www.w3schools.com/html/html_forms.asp"));
    }

    @Test
    public void pageObjectGenerator_XpathSelenideElementWithDefaultAnnotation_Success()
        throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/selenide_element_xpath_default_annotation.json", outputDir,
            Collections.singletonList("https://www.w3schools.com/html/html_forms.asp"));
    }

    @Test
    public void pageObjectGenerator_XpathSelenideElementWithDifferentElements_Success()
        throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/selenide_element_xpath_different_elements.json", outputDir,
            Collections.singletonList("https://www.w3schools.com/html/html_forms.asp"));
    }

    @Test
    public void pageObjectGenerator_TableRows_Success()
            throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/webelements_table.json", outputDir,
                Collections.singletonList("https://www.w3schools.com/html/html_tables.asp"));
    }

    @Test
    public void checkGeneratedFormattedFile_StatusOk() throws Exception {
        pog.setForceGenerateFile(false);
        pog.generatePageObjects("/elements_to_create_formatted_file.json", outputDir,
                Collections.singletonList("https://epam.github.io/JDI/metals-colors.html"));

        JavaFileWriter javaFileWriter = pog.getJavaFileWriter();
        List<JavaFile> javaFiles = javaFileWriter.getJavaFiles();

        for (JavaFile javaFile : javaFiles) {
            Path p = javaFileWriter.getFullPath(Paths.get(outputDir), javaFile);
            String file  = FileUtils.readFileToString(new File(p.toUri()));
            assertThat("Formatted file is empty",
                    file, not(equalTo("")));

            List<AnnotationSpec> annotations = javaFile.typeSpec.annotations;

            for (AnnotationSpec annotationSpec : annotations) {
                String annotationType = annotationSpec.type.toString();
                String annotation = annotationType.substring(
                        annotationType.lastIndexOf('.') + 1);
                assertThat("Formatted file does not contain build annotation",
                        file, containsString(annotation));
            }
        }
    }
}