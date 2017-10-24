package com.epam.page.object.generator;

import static com.epam.page.object.generator.builder.StringUtils.firstLetterDown;
import static com.epam.page.object.generator.builder.StringUtils.firstLetterUp;
import static com.epam.page.object.generator.builder.StringUtils.splitCamelCase;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

import com.epam.jdi.uitests.web.selenium.elements.common.Button;
import com.epam.jdi.uitests.web.selenium.elements.common.CheckBox;
import com.epam.jdi.uitests.web.selenium.elements.common.DatePicker;
import com.epam.jdi.uitests.web.selenium.elements.common.FileInput;
import com.epam.jdi.uitests.web.selenium.elements.common.Image;
import com.epam.jdi.uitests.web.selenium.elements.common.Input;
import com.epam.jdi.uitests.web.selenium.elements.common.Label;
import com.epam.jdi.uitests.web.selenium.elements.common.Link;
import com.epam.jdi.uitests.web.selenium.elements.common.Text;
import com.epam.jdi.uitests.web.selenium.elements.common.TextArea;
import com.epam.jdi.uitests.web.selenium.elements.common.TextField;
import com.epam.jdi.uitests.web.selenium.elements.complex.CheckList;
import com.epam.jdi.uitests.web.selenium.elements.complex.ComboBox;
import com.epam.jdi.uitests.web.selenium.elements.complex.DropList;
import com.epam.jdi.uitests.web.selenium.elements.complex.Dropdown;
import com.epam.jdi.uitests.web.selenium.elements.complex.Elements;
import com.epam.jdi.uitests.web.selenium.elements.complex.RadioButtons;
import com.epam.jdi.uitests.web.selenium.elements.complex.Selector;
import com.epam.jdi.uitests.web.selenium.elements.complex.Tabs;
import com.epam.jdi.uitests.web.selenium.elements.complex.TextList;
import com.epam.jdi.uitests.web.selenium.elements.complex.table.Table;
import com.epam.jdi.uitests.web.selenium.elements.composite.WebPage;
import com.epam.jdi.uitests.web.selenium.elements.composite.WebSite;
import com.epam.jdi.uitests.web.selenium.elements.pageobjects.annotations.JPage;
import com.epam.jdi.uitests.web.selenium.elements.pageobjects.annotations.JSite;
import com.epam.page.object.generator.builder.FieldsBuilder;
import com.epam.page.object.generator.builder.IFieldsBuilder;
import com.epam.page.object.generator.errors.ValidationException;
import com.epam.page.object.generator.model.SearchRule;
import com.epam.page.object.generator.parser.JSONIntoRuleParser;
import com.epam.page.object.generator.validators.SearchRuleValidator;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class PageObjectsGenerator {

    private JSONIntoRuleParser parser;

    private List<String> urls;
    private String outputDir;
	private String packageName;

    private boolean forceGenerateFile;

    private Map<String, IFieldsBuilder> builders = new HashMap<>();

    public PageObjectsGenerator(String jsonPath, List<String> urls, String outputDir, String packageName) {
        builders.put("button", new FieldsBuilder(Button.class));
        builders.put("text", new FieldsBuilder(Text.class));
        builders.put("checkbox", new FieldsBuilder(CheckBox.class));
        builders.put("image", new FieldsBuilder(Image.class));
        builders.put("datepicker", new FieldsBuilder(DatePicker.class));
        builders.put("fileinput", new FieldsBuilder(FileInput.class));
        builders.put("input", new FieldsBuilder(Input.class));
        builders.put("label", new FieldsBuilder(Label.class));
        builders.put("link", new FieldsBuilder(Link.class));
        builders.put("textarea", new FieldsBuilder(TextArea.class));
        builders.put("textfield", new FieldsBuilder(TextField.class));
        builders.put("radiobuttons", new FieldsBuilder(RadioButtons.class));
        builders.put("selector", new FieldsBuilder(Selector.class));
        builders.put("tabs", new FieldsBuilder(Tabs.class));
        builders.put("textlist", new FieldsBuilder(TextList.class));
        builders.put("table", new FieldsBuilder(Table.class));
        builders.put("checklist", new FieldsBuilder(CheckList.class));
        builders.put("combobox", new FieldsBuilder(ComboBox.class));
        builders.put("dropdown", new FieldsBuilder(Dropdown.class));
        builders.put("droplist", new FieldsBuilder(DropList.class));
        builders.put("elements", new FieldsBuilder(Elements.class));

        parser = new JSONIntoRuleParser(jsonPath, builders.keySet());
        this.urls = urls;
        this.outputDir = outputDir;
        this.packageName = packageName;
    }

    public PageObjectsGenerator addBuilder(String name, IFieldsBuilder builder) {
        builders.put(name.toLowerCase(), builder);
        parser.getSupportedTypes().add(name.toLowerCase());

        return this;
    }

    /**
     * Generates .java file with all HTML-elements found on the web-site by rules given by user in
     * .json file.
     *
     * @throws IOException If .json file could not be opened or written to .java file.
     * @throws URISyntaxException If urls could not be parsed as URI references.
     */
    public void generatePageObjects(boolean forceGenerateFile) throws IOException, URISyntaxException, ValidationException {
        this.forceGenerateFile = forceGenerateFile;

        List<SearchRule> searchRules = parser.getRulesFromJSON();

        try {
            SearchRuleValidator.validate(searchRules);
        } catch (ValidationException ex) {
            if (forceGenerateFile) {
                generateJavaFiles(searchRules);
            }
            throw ex;
        }

		generateJavaFiles(searchRules);
    }

    private void generateJavaFiles(List<SearchRule> searchRules) throws IOException, URISyntaxException {
        List<FieldSpec> siteClassFields = new ArrayList<>();

        for (String url : urls) {
            String titleName = splitCamelCase(getPageTitle(url));
            String pageFieldName = firstLetterDown(titleName);
            String pageClassName = firstLetterUp(titleName);
            ClassName pageClass = createPageClass(pageClassName, searchRules, url);

            siteClassFields.add(FieldSpec.builder(pageClass, pageFieldName)
                .addModifiers(PUBLIC, STATIC)
                .addAnnotation(AnnotationSpec.builder(JPage.class)
                    .addMember("url", "$S", getUrlWithoutDomain(url))
                    .addMember("title", "$S", getPageTitle(url))
                    .build())
                .build());
        }

        TypeSpec siteClass = TypeSpec.classBuilder("Site")
            .addModifiers(PUBLIC)
            .addAnnotation(AnnotationSpec.builder(JSite.class)
                .addMember("domain", "$S", getDomainName())
                .build())
            .superclass(WebSite.class)
            .addFields(siteClassFields)
            .build();

        JavaFile javaFile = JavaFile.builder(packageName + ".site", siteClass)
            .build();

        javaFile.writeTo(Paths.get(outputDir));
    }

    /**
     * Generates one of the nested classes with all HTML-elements found on the web-page with
     * following url by rules.
     *
     * @param pageClassName Name of page class.
     * @param searchRules List of rules.
     * @param url One of the web-pages of web-site.
     * @return generated page class.
     * @throws IOException If can't write java file.
     */
    private ClassName createPageClass(String pageClassName, List<SearchRule> searchRules, String url) throws IOException {
        List<FieldSpec> fields = new ArrayList<>();

        for (SearchRule searchRule : searchRules) {
            fields.addAll(
                builders.get(searchRule.getType().toLowerCase()).buildField(searchRule, url));
        }

        TypeSpec pageClass = TypeSpec.classBuilder(pageClassName)
            .addModifiers(PUBLIC)
            .superclass(WebPage.class)
            .addFields(fields)
            .build();
        JavaFile javaFile = JavaFile.builder(packageName + ".pages", pageClass)
            .build();

        javaFile.writeTo(Paths.get(outputDir));

        return ClassName.get(packageName + ".pages", pageClassName);
    }

    /**
     * Returns URL without it's domain part.
     *
     * @param url One of the web-pages of web-site.
     * @return URL without domain part.
     * @throws URISyntaxException If url could not be parsed as a URI reference.
     */
    private String getUrlWithoutDomain(String url) throws URISyntaxException {
        return new URI(url).getPath();
    }

    /**
     * Extracts domain URL from list of URLs of the web-site.
     *
     * @return domain URL.
     * @throws URISyntaxException If url could not be parsed as a URI reference.
     */
    private String getDomainName() throws URISyntaxException {
        return new URI(urls.get(0)).getHost();
    }

    private String getPageTitle(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        return document.title();
    }

}