package com.sonsure.dumper.test.common.validation;

import com.sonsure.dumper.common.exception.ValidationException;
import com.sonsure.dumper.common.validation.ValidationError;
import com.sonsure.dumper.common.validation.ValidationGroup;
import com.sonsure.dumper.common.validation.ValidationResult;
import com.sonsure.dumper.common.validation.Verifier;
import com.sonsure.dumper.test.model.ValidationModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerifierTest {

    @Test
    public void notNull() {
        String message = "值不能为空";
        Verifier.init().notNull("aa", message).validate();
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().notNull(null, message).validate();
        });
        Assertions.assertEquals(message, exception.getMessage());
    }

    @Test
    public void notEmpty() {
        ArrayList<Object> list = new ArrayList<>();
        list.add(1);
        Map<String, Object> map = new HashMap<>();
        map.put("key", "value");

        ArrayList<Object> emptyList = new ArrayList<>();
        Map<String, Object> emptyMap = new HashMap<>();
        String message = "对象不能为空";
        Verifier.init().notEmpty(list, message)
                .notEmpty(map, message)
                .notEmpty(new Object[]{1, 2}, message)
                .validate();

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> Verifier.init().notEmpty(null, message).validate());
        Assertions.assertEquals(message, ex1.getMessage());

        Exception ex2 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().notEmpty(emptyList, message).validate();
        });
        Assertions.assertEquals(message, ex2.getMessage());

        Exception ex3 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().notEmpty(emptyMap, message).validate();
        });
        Assertions.assertEquals(message, ex3.getMessage());
    }

    @Test
    public void notBlank() {
        String message = "值不能为空";
        Verifier.init().notBlank("aaa", message).validate();

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().notBlank(null, message).validate();
        });
        Assertions.assertEquals(message, ex1.getMessage());

        Exception ex2 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().notBlank("", message).validate();
        });
        Assertions.assertEquals(message, ex2.getMessage());

        Exception ex3 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().notBlank("   ", message).validate();
        });
        Assertions.assertEquals(message, ex3.getMessage());
    }

    @Test
    public void expectFalse() {
        String message = "必须为false";
        Verifier.init().expectFalse(false, message).validate();

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().expectFalse(true, message).validate();
        });
        Assertions.assertEquals(message, ex1.getMessage());
    }

    @Test
    public void expectTrue() {
        String message = "必须为true";
        Verifier.init().expectTrue(true, message).validate();

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().expectTrue(false, message).validate();
        });
        Assertions.assertEquals(message, ex1.getMessage());
    }

    @Test
    public void eachElNotNull() {
        String message = "不能有null元素";

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().eachElNotNull(null, message).validate();
        });
        Assertions.assertEquals(message, ex1.getMessage());

        ArrayList<Object> list = new ArrayList<>();
        list.add(111);
        Verifier.init().eachElNotNull(list, message).validate();

        list.add(null);
        Exception ex2 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().eachElNotNull(list, message).validate();
        });
        Assertions.assertEquals(message, ex2.getMessage());
    }


    @Test
    public void regexMatch() {
        String message = "只能包含数字";
        String regex = "^\\d+$";
        Verifier.init().regexMatch("123456", regex, message).validate();

        Exception ex2 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().regexMatch("a123456", regex, message).validate();
        });
        Assertions.assertEquals(message, ex2.getMessage());

    }


    @Test
    public void with() {
        String message = "不能为空";
        Verifier.init().notBlank("  ", message).with(false).validate();

        Exception ex2 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().notBlank("   ", message).with(true).validate();
        });
        Assertions.assertEquals(message, ex2.getMessage());
    }

    @Test
    public void errorCode() {
        String message = "aa不能为空";
        String errorCode = "val.eq.result";
        ValidationException ex2 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().notBlank("", message).errorCode(errorCode).validate();
        });
        Assertions.assertEquals(errorCode, ex2.getResultCode());
    }

    @Test
    public void errorMessage() {
        String message = "不能为空";
        String errorMsg = "aa不能为空";
        ValidationException ex2 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().notBlank("", message).errorMessage(errorMsg).validate();
        });
        Assertions.assertEquals(errorMsg, ex2.getResultMsg());
    }

    @Test
    public void result() {
        String message = "不能为空";
        ValidationResult result = Verifier.init().notBlank("", message).result();
        List<ValidationError> errors = result.getErrors();
        Assertions.assertEquals(1, errors.size());
        Assertions.assertEquals(message, errors.iterator().next().getErrorMsg());
    }

    @Test
    public void validate() {
        ValidationModel validationModel = new ValidationModel();
        List<String> validate = Verifier.jsrValidate(validationModel, false, ValidationGroup.defaults(), ValidationGroup.Update.class);
        Assertions.assertEquals(4, validate.size());

        validationModel.setPassword("123456");
        List<String> validate1 = Verifier.jsrValidate(validationModel, false);
        Assertions.assertEquals(2, validate1.size());

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.jsrValidate(validationModel, ValidationGroup.Update.class);
        });
        Assertions.assertEquals("id不能为空", ex1.getMessage());
    }
}
