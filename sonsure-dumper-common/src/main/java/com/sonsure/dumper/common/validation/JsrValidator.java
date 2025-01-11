package com.sonsure.dumper.common.validation;

import com.sonsure.dumper.common.exception.ValidationException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author selfly
 * <p>
 * The type Jsr validator.
 */
public class JsrValidator {

    private static Validator validator;

    public static <T> List<String> validate(T t, Class<?>... groups) {
        return validate(t, true, groups);
    }

    public static <T> List<String> validate(T t, boolean throwsExp, Class<?>... groups) {
        if (validator == null) {
            try (ValidatorFactory vf = Validation.buildDefaultValidatorFactory()) {
                validator = vf.getValidator();
            }
        }
        Set<ConstraintViolation<T>> errors;
        if (groups != null && groups.length > 0) {
            errors = validator.validate(t, groups);
        } else {
            errors = validator.validate(t);
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
