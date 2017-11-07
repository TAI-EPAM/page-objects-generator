package com.epam.page.object.generator.validators;

import com.epam.page.object.generator.model.SearchRule;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocatorExistenceValidatorTest {
	SearchRule ruleWithCss = new SearchRule("type", "req", "css", null, null);
	SearchRule ruleWithXpath = new SearchRule("type", "req", null, "//input", null);
	SearchRule ruleNoLocator = new SearchRule("type", "req", null, null, null);
	private LocatorExistenceValidator sut;
	private ValidationContext context;
	private List<SearchRule> rules = Lists.newArrayList();


	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		sut = new LocatorExistenceValidator();
	}

	@Test
	public void validate_allPassIfOnlyCssIsSet() throws Exception {
		context = new ValidationContext(Lists.newArrayList(ruleWithCss), null);
		sut.validate(context);
		assertEquals(ruleWithCss, context.getValidRules().get(0));
	}

	@Test
	public void validate_allPassIfOnlyXpathIsSet() throws Exception {
		context = new ValidationContext(Lists.newArrayList(ruleWithXpath), null);
		sut.validate(context);
		assertEquals(ruleWithXpath, context.getValidRules().get(0));
	}

	@Test
	public void validate_shouldAddSearchRuleToFailedIfNoLocatorSet() throws Exception {
		context = new ValidationContext(Lists.newArrayList(ruleNoLocator), null);
		sut.validate(context);
		assertEquals(ruleNoLocator, context.getNotValidRules().get(0));
	}

	@Test
	public void validate_checkAllValidAndInvalidRulesLists() throws Exception {
		rules.add(ruleWithCss);
		rules.add(ruleWithXpath);
		rules.add(ruleNoLocator);

		context = new ValidationContext(rules, null);

		sut.validate(context);
		assertEquals(2, context.getValidRules().size());
		assertEquals(1, context.getNotValidRules().size());
	}

	@Test
	public void validate_TrueSearchRuleValidationWithCss(){
		assertTrue(sut.validate(ruleWithCss));
	}

	@Test
	public void validate_TrueSearchRuleValidationWithXpath(){
		assertTrue(sut.validate(ruleWithXpath));
	}

	@Test
	public void validate_FalseSearchRuleValidationNoLocators(){
		assertFalse(sut.validate(ruleNoLocator));
	}
}