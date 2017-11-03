package com.epam.page.object.generator.utils;

import static org.junit.Assert.*;

import com.epam.page.object.generator.model.SearchRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SelectorUtilsTest {

    private SearchRule searchRule = new SearchRule();
    private String elementsRequiredValue;

    @Before
    public void setUp() {
        elementsRequiredValue = "button";
        searchRule.setCss("css");
        searchRule.setXpath("//input[@type='submit']");
        searchRule.setRequiredAttribute("name");
    }

    @Test
    public void resultCssSelector_success() throws Exception {
        String expected = "css[name='button']";

        Assert.assertEquals(expected, SelectorUtils.resultCssSelector(searchRule, elementsRequiredValue));

    }

    @Test
    public void resultXpathSelector() throws Exception {
        String expected = "//input[@type='submit' and @name='button']";

        Assert.assertEquals(expected, SelectorUtils.resultXpathSelector(searchRule, elementsRequiredValue));
    }

}