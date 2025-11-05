package com.sonsure.dustman.common.validation;

import com.sonsure.dustman.common.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * The type Javax validator adapter.
 *
 * @author selfly
 */
public class JakartaValidatorAdapter implements ValidatorAdapter {

    private final Validator validator;

    public JakartaValidatorAdapter() {
        try (ValidatorFactory vf = Validation.buildDefaultValidatorFactory()) {
            validator = vf.getValidator();
        }
    }

    @Override
    public <T> List<String> validate(T object, boolean throwsExp, Class<?>... groups) {
        Set<ConstraintViolation<T>> errors;
        if (groups != null && groups.length > 0) {
            errors = validator.validate(object, groups);
        } else {
            errors = validator.validate(object);
        }
        if (errors.isEmpty()) {
            return Collections.emptyList();
        }
        if (throwsExp) {
            throw new ValidationException(errors.iterator().next().getMessage());
        }
        List<String> validateError = new ArrayList<>();
        for (ConstraintViolation<T> val : errors) {
            validateError.add(val.getMessage());
        }
        return validateError;
    }
}
