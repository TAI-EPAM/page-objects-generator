package com.epam.page.object.generator.validator;

import com.epam.page.object.generator.model.searchrule.*;
import com.epam.page.object.generator.model.webgroup.CommonWebElementGroup;
import com.epam.page.object.generator.model.webgroup.ComplexWebElementGroup;
import com.epam.page.object.generator.model.webgroup.FormWebElementGroup;
import com.epam.page.object.generator.model.webgroup.SelenideWebElementGroup;
import com.epam.page.object.generator.model.webgroup.SelenideWebElementsCollectionGroup;

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

    default ValidationResult visit(SelenideWebElementsCollectionGroup selenideWebElementGroup) {
        return new ValidationResult(true, this + " passed!");
    }

    default ValidationResult visit(SelenideElementsCollectionSearchRule selenideWebElementGroup) {
        return new ValidationResult(true, this + " passed!");
    }
}
