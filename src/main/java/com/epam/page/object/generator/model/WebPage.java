package com.epam.page.object.generator.model;

import com.epam.page.object.generator.adapter.classbuildable.FormClassBuildable;
import com.epam.page.object.generator.adapter.classbuildable.JavaClassBuildable;
import com.epam.page.object.generator.model.searchrule.FormSearchRule;
import com.epam.page.object.generator.model.searchrule.Validatable;
import com.epam.page.object.generator.model.webgroup.FormWebElementGroup;
import com.epam.page.object.generator.model.webgroup.WebElementGroup;
import com.epam.page.object.generator.util.SearchRuleExtractor;
import com.epam.page.object.generator.util.SelectorUtils;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Document;
import com.epam.page.object.generator.model.searchrule.SearchRule;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents web-page (which POG is parsing) with it's URI and list of {@link WebElementGroup}
 * elements.
 */

public class WebPage {

    private final URI uri;

    private Document document;

    /**
     * List of {@link WebElementGroup} elements. Every web group represents group of page
     * elements, which was found with one of the {@link SearchRule} search rules. Every search
     * rule corresponds to web element group
     */
    private List<WebElementGroup> webElementGroups;

    /**
     * Extract page elements using search rules.
     */
    private SearchRuleExtractor searchRuleExtractor;

    private final static Logger logger = LoggerFactory.getLogger(WebPage.class);

    public WebPage(URI uri, Document document,
                   SearchRuleExtractor searchRuleExtractor) {
        this.searchRuleExtractor = searchRuleExtractor;
        this.webElementGroups = new ArrayList<>();
        this.uri = uri;
        this.document = document;
    }

    public String getTitle() {
        return document.title();
    }

    public String getUrlWithoutDomain() {
        return uri.getPath();
    }

    public String getDomainName() {
        return uri.getHost();
    }

    public List<WebElementGroup> getWebElementGroups() {
        return webElementGroups;
    }

    /**
     * Get list of {@link SearchRule} and fill web element group {@link WebElementGroup }
     * @param searchRules - search rules which is used by method to parse current web-page.
     */
    public void addSearchRules(List<SearchRule> searchRules) {
        for (SearchRule searchRule : searchRules) {
            Elements elements = searchRuleExtractor
                .extractElementsFromElement(document, searchRule);
            if (elements.size() != 0) {
                searchRule.fillWebElementGroup(webElementGroups, elements);
            }
        }
    }

    /**
     * Checks if web-page contains forms.
     * If it's not - no need to create separated form classes.
     */
    public boolean isContainedFormSearchRule() {
        return webElementGroups.stream()
            .anyMatch(webElementGroup -> webElementGroup.getSearchRule() instanceof FormSearchRule);
    }

    /**
     * Validates all web element groups of current page
     */
    public boolean hasInvalidWebElementGroup() {
        return webElementGroups.stream().anyMatch(Validatable::isInvalid);
    }

    /**
     * Generates list of {@link JavaClassBuildable} for every form found on a page.
     * @param selectorUtils {@link SelectorUtils} object to parse selector of
     * webElementGroup search rule
     */
    public List<JavaClassBuildable> getFormClasses(SelectorUtils selectorUtils) {
        List<JavaClassBuildable> javaClasses = new ArrayList<>();

        for (WebElementGroup webElementGroup : webElementGroups) {
            if (webElementGroup instanceof FormWebElementGroup) {
                FormWebElementGroup elementGroup = (FormWebElementGroup) webElementGroup;
                logger.debug("Start creating FormClassBuildable for '" + elementGroup.getSearchRule().getSection() + "' form");
                javaClasses.add(new FormClassBuildable(elementGroup,
                    selectorUtils));
                logger.debug("Finish creating FormClassBuildable");
            }
        }
        return javaClasses;
    }
}
