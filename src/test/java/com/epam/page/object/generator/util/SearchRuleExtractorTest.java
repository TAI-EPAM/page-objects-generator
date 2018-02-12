package com.epam.page.object.generator.util;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.epam.jdi.uitests.web.selenium.elements.common.Button;
import com.epam.jdi.uitests.web.selenium.elements.complex.Dropdown;
import com.epam.page.object.generator.adapter.JavaField;
import com.epam.page.object.generator.adapter.classbuildable.PageClass;
import com.epam.page.object.generator.builder.WebElementGroupFieldBuilder;
import com.epam.page.object.generator.builder.webpage.LocalWebPageBuilder;
import com.epam.page.object.generator.model.ClassAndAnnotationPair;
import com.epam.page.object.generator.model.Selector;
import com.epam.page.object.generator.model.WebPage;
import com.epam.page.object.generator.model.searchrule.CommonSearchRule;
import com.epam.page.object.generator.model.searchrule.ComplexInnerSearchRule;
import com.epam.page.object.generator.model.searchrule.ComplexSearchRule;
import com.epam.page.object.generator.model.searchrule.SearchRule;
import static org.hamcrest.MatcherAssert.assertThat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.support.FindBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SearchRuleExtractorTest {

    private static final String PATH_TO_HTML = "src/test/resources/html/complex_element.html";

    @Mock
    private Element element;

    @Mock
    private Elements elements;

    private XpathToCssTransformer transformer = new XpathToCssTransformer();
    private SelectorUtils selectorUtils = new SelectorUtils();

    private SearchRule searchRuleWithCss = new CommonSearchRule(
            "text",
            SearchRuleType.BUTTON,
            new Selector("css", ".myClass"),
            new ClassAndAnnotationPair(Button.class, FindBy.class),
            transformer,
            selectorUtils
    );

    private SearchRule searchRuleWithXpath = new CommonSearchRule(
            "text",
            SearchRuleType.BUTTON,
            new Selector("xpath", "//input[@type='submit']"),
            new ClassAndAnnotationPair(Button.class, FindBy.class),
            transformer,
            selectorUtils
    );

    private List<ComplexInnerSearchRule> innerSearchRules_valid = new ArrayList<ComplexInnerSearchRule>() {
        {
            add(new ComplexInnerSearchRule(
                    "title",
                    "root",
                    new Selector("css", "button[data-id=colors-dropdown]"),
                    transformer
            ));
            add(new ComplexInnerSearchRule(
                    null,
                    "list",
                    new Selector("xpath", "//ul[@class='dropdown-menu inner selectpicker']"),
                    transformer
            ));
        }
    };

    private ComplexSearchRule complexSearchRule = new ComplexSearchRule(
            SearchRuleType.DROPDOWN,
            innerSearchRules_valid,
            new ClassAndAnnotationPair(Dropdown.class, FindBy.class),
            selectorUtils
    );

    private SearchRuleExtractor searchRuleExtractor = new SearchRuleExtractor();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void extractElementsFromElement_SearchRuleWithXpath_NotNull() {
        String html = "<input value='Поиск в Google' aria-label='Поиск в Google' name='btnK' type='submit' jsaction='sf.chk'/>";
        Document doc = Jsoup.parse(html);
        Element buttonElement = doc.select("input[type=submit]").first();
        Elements elements = searchRuleExtractor
                .extractElementsFromElement(buttonElement, searchRuleWithXpath);

        assertThat("Something went wrong",
                elements, notNullValue());
    }

    @Test
    public void extractComplexElementWithRightInnerElements_Valid() throws IOException {
        String html = Files.lines(Paths.get(PATH_TO_HTML)).collect(
                Collectors.joining());

        searchRuleExtractor.extractElementsFromElement(Jsoup.parse(html), complexSearchRule);

        assertThat("Something went wrong, invalid amount of inner search rules",
                complexSearchRule.getInnerSearchRules(), hasSize(2));
    }

    @Test
    public void extractComplexElementWithWrongInnerElement_Valid() throws IOException {
        String html = Files.lines(Paths.get(PATH_TO_HTML)).collect(
                Collectors.joining());

        List<ComplexInnerSearchRule> innerSearchRules_invalid = innerSearchRules_valid;
        innerSearchRules_invalid.add(new ComplexInnerSearchRule(
                null,
                "value",
                new Selector("xpath", "//test"),
                transformer));

        assertThat("Something went wrong, invalid amount of inner search rules",
                complexSearchRule.getInnerSearchRules(), hasSize(3));

        searchRuleExtractor.extractElementsFromElement(Jsoup.parse(html), complexSearchRule);

        assertThat("Something went wrong, invalid amount of inner search rules",
                complexSearchRule.getInnerSearchRules(), hasSize(2));
    }

    @Test
    public void buildAnnotationComplexElementWithRightInnerElements() throws IOException {
        LocalWebPageBuilder builder = new LocalWebPageBuilder();
        List<WebPage> webPages = builder.generate(Collections.singletonList("/html/complex_element.html"), searchRuleExtractor);
        List<SearchRule> searchRules = new ArrayList<>();
        searchRules.add(complexSearchRule);
        webPages.forEach(wp -> wp.addSearchRules(searchRules));
        PageClass page = new PageClass(webPages.get(0), new WebElementGroupFieldBuilder());
        List<JavaField> fields = page.buildFields("test");
        assertThat("Annotation has not been built appropriately",
                fields.get(0).getAnnotation(), notNullValue());
    }

    @Test
    public void buildAnnotationComplexElementWithWrongInnerElement() throws IOException {
        LocalWebPageBuilder builder = new LocalWebPageBuilder();
        List<WebPage> webPages = builder.generate(Collections.singletonList("/html/complex_element.html"), searchRuleExtractor);
        List<SearchRule> searchRules = new ArrayList<>();
        searchRules.add(complexSearchRule);

        List<ComplexInnerSearchRule> innerSearchRules_invalid = innerSearchRules_valid;
        innerSearchRules_invalid.add(new ComplexInnerSearchRule(
                null,
                "value",
                new Selector("xpath", "//test"),
                transformer));

        webPages.forEach(wp -> wp.addSearchRules(searchRules));
        PageClass page = new PageClass(webPages.get(0), new WebElementGroupFieldBuilder());
        List<JavaField> fields = page.buildFields("test");
        assertThat("Annotation has not been built appropriately",
                fields.get(0).getAnnotation(), notNullValue());
    }

    @Test
    public void extractElementsFromElement_SearchRuleWithCss_StatusOk() {
        when(element.select(anyString())).thenReturn(elements);

        searchRuleExtractor.extractElementsFromElement(element, searchRuleWithCss);

        verify(element).select(eq(searchRuleWithCss.getSelector().getValue()));
    }
}