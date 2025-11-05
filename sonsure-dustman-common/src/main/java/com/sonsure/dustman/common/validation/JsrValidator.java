package com.sonsure.dustman.common.validation;

import java.util.List;

/**
 * @author selfly
 * <p>
 * The type Jsr validator.
 */
public class JsrValidator {

    private static ValidatorAdapter validator;

    static {
        try {
            Class.forName("jakarta.validation.Validator");
            validator = new JakartaValidatorAdapter();
        } catch (ClassNotFoundException e) {
            validator = new JavaxValidatorAdapter();
        }

    }

    public static <T> List<String> validate(T t, Class<?>... groups) {
        return validate(t, true, groups);
    }

    public static <T> List<String> validate(T t, boolean throwsExp, Class<?>... groups) {
        return validator.validate(t, throwsExp, groups);
    }
}
