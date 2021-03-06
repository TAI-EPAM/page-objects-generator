package com.epam.page.object.generator.validator;

import com.epam.page.object.generator.model.searchrule.*;
import com.epam.page.object.generator.model.webgroup.*;
import com.epam.page.object.generator.model.searchrule.CommonSearchRule;
import com.epam.page.object.generator.model.searchrule.ComplexInnerSearchRule;
import com.epam.page.object.generator.model.searchrule.ComplexSearchRule;
import com.epam.page.object.generator.model.searchrule.FormInnerSearchRule;
import com.epam.page.object.generator.model.searchrule.FormSearchRule;
import com.epam.page.object.generator.model.searchrule.Validatable;
import com.epam.page.object.generator.model.searchrule.WebElementsSearchRule;
import com.epam.page.object.generator.model.webgroup.CommonWebElementGroup;
import com.epam.page.object.generator.model.webgroup.ComplexWebElementGroup;
import com.epam.page.object.generator.model.webgroup.FormWebElementGroup;
import com.epam.page.object.generator.model.webgroup.WebElementsElementGroup;

/**
 * Realization of visitor pattern. Here we call method visit for all types of searchRules to
 * validate them.
 *
 * If you add a new {@link Validatable} object, you have to add here method visit for it.
 */
public interface ValidatorVisitor {

    default ValidationResult visit(CommonSearchRule commonSearchRule) {
        return new ValidationResult(true, this + " passed!");
    }

    default ValidationResult visit(WebElementsSearchRule webElementsSearchRule) {
        return new ValidationResult(true, this + " passed!");
    }

    default ValidationResult visit(ComplexSearchRule complexSearchRule) {
        return new ValidationResult(true, this + " passed!");
    }

    default ValidationResult visit(ComplexInnerSearchRule complexInnerSearchRule) {
        return new ValidationResult(true, this + " passed!");
    }

    default ValidationResult visit(FormSearchRule formSearchRule) {
        return new ValidationResult(true, this + " passed!");
    }

    default ValidationResult visit(FormInnerSearchRule formInnerSearchRule) {
        return new ValidationResult(true, this + " passed!");
    }

    default ValidationResult visit(CommonWebElementGroup commonWebElementGroup) {
        return new ValidationResult(true, this + " passed!");
    }

    default ValidationResult visit(ComplexWebElementGroup complexWebElementGroup) {
        return new ValidationResult(true, this + " passed!");
    }

    default ValidationResult visit(FormWebElementGroup formWebElementGroup) {
        return new ValidationResult(true, this + " passed!");
    }

    default ValidationResult visit(SelenideSearchRule selenideSearchRule) {
        return new ValidationResult(true, this + " passed!");
    }

    default ValidationResult visit(SelenideWebElementGroup selenideWebElementGroup) {
        return new ValidationResult(true, this + " passed!");
    }

    default ValidationResult visit(SelenideElementsCollectionSearchRule selenideElementsCollectionSearchRule) {
        return new ValidationResult(true, this + " passed!");
    }

    default ValidationResult visit(SelenideElementsCollectionWebElementGroup selenideElementsCollectionWebElementGroup) {
        return new ValidationResult(true, this + " passed!");
    }

    default ValidationResult visit(WebElementsElementGroup webElementsElementGroup) {
        return new ValidationResult(true, this + " passed!");
    }
}
