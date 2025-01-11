package com.sonsure.dumper.test.validation;

import com.sonsure.dumper.common.exception.ValidationException;
import com.sonsure.dumper.common.validation.ValidationError;
import com.sonsure.dumper.common.validation.ValidationGroup;
import com.sonsure.dumper.common.validation.ValidationResult;
import com.sonsure.dumper.common.validation.Verifier;
import com.sonsure.dumper.test.model.ValidationModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.groups.Default;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerifierTest {

    private final Object VAL_NULL = null;
    private final Object VAL_STR = "selfly";


//    public static void assertNotNull(Object obj, String message) {
//        Verifier.init().isNotNull(obj, message).validate();
//    }
//
//    public static void assertNotBlank(String str, String message) {
//        Verifier.init().isNotBlank(str, message).validate();
//    }

    @Test
    public void isNotNull() {
        String message = "值不能为空";
        Verifier.init().isNotNull(VAL_STR, message).validate();
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isNotNull(VAL_NULL, message).validate();
        });
        Assertions.assertEquals(message, exception.getMessage());
    }

    @Test
    public void isNotEmpty() {
        ArrayList<Object> list = new ArrayList<>();
        list.add(1);
        Map<String, Object> map = new HashMap<>();
        map.put("key", "value");

        ArrayList<Object> emptyList = new ArrayList<>();
        Map<String, Object> emptyMap = new HashMap<>();
        String message = "对象不能为空";
        Verifier.init().isNotEmpty(list, message)
                .isNotEmpty(map, message)
                .isNotEmpty(new Object[]{1, 2}, message)
                .isNotEmpty("aa", message)
                .validate();

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isNotEmpty(VAL_NULL, message).validate();
        });
        Assertions.assertEquals(message, ex1.getMessage());

        Exception ex2 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isNotEmpty(emptyList, message).validate();
        });
        Assertions.assertEquals(message, ex2.getMessage());

        Exception ex3 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isNotEmpty(emptyMap, message).validate();
        });
        Assertions.assertEquals(message, ex3.getMessage());
    }

    @Test
    public void isNotBlank() {
        String message = "值不能为空";
        Verifier.init().isNotBlank("aaa", message).validate();

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isNotBlank(null, message).validate();
        });
        Assertions.assertEquals(message, ex1.getMessage());

        Exception ex2 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isNotBlank("", message).validate();
        });
        Assertions.assertEquals(message, ex2.getMessage());

        Exception ex3 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isNotBlank("   ", message).validate();
        });
        Assertions.assertEquals(message, ex3.getMessage());
    }

    @Test
    public void isMinLength() {
        String message = "最小长度必须大于{0}";
        int minLength = 2;
        Verifier.init().isMinLength("aaa", minLength, message).validate();

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isMinLength(null, minLength, message).validate();
        });
        Assertions.assertEquals(MessageFormat.format(message, minLength), ex1.getMessage());

        Exception ex2 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isMinLength("a", minLength, message).validate();
        });
        Assertions.assertEquals(MessageFormat.format(message, minLength), ex2.getMessage());
    }

    @Test
    public void isMaxLength() {
        String message = "最大长度必须小于{0}";
        int minLength = 5;
        Verifier.init().isMaxLength("aaa", minLength, message).validate();
        Verifier.init().isMaxLength(null, minLength, message).validate();

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isMaxLength("aaaaaaa", minLength, message).validate();
        });
        Assertions.assertEquals(MessageFormat.format(message, minLength), ex1.getMessage());
    }

    @Test
    public void isArrMinLength() {
        String message = "最小长度必须大于{0}";
        int minLength = 2;
        Object[] arr1 = new Object[]{1, 2, 3};
        Object[] arr2 = new Object[]{1};
        Verifier.init().isArrMinLength(arr1, minLength, message).validate();

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isArrMinLength(null, minLength, message).validate();
        });
        Assertions.assertEquals(MessageFormat.format(message, minLength), ex1.getMessage());

        Exception ex2 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isArrMinLength(arr2, minLength, message).validate();
        });
        Assertions.assertEquals(MessageFormat.format(message, minLength), ex2.getMessage());
    }

    @Test
    public void isArrMaxLength() {
        String message = "最大长度必须小于{0}";
        int minLength = 5;
        Object[] arr1 = new Object[]{1, 2, 3};
        Object[] arr2 = new Object[]{1, 2, 3, 4, 5, 6, 7};
        Verifier.init().isArrMaxLength(arr1, minLength, message).validate();
        Verifier.init().isArrMaxLength(null, minLength, message).validate();

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isArrMaxLength(arr2, minLength, message).validate();
        });
        Assertions.assertEquals(MessageFormat.format(message, minLength), ex1.getMessage());
    }

    @Test
    public void isEqLength() {
        String message = "长度必须等于{0}";
        int minLength = 5;
        Verifier.init().isEqLength("aaaaa", minLength, message).validate();

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isEqLength("aaaaaaa", minLength, message).validate();
        });
        Assertions.assertEquals(MessageFormat.format(message, minLength), ex1.getMessage());
    }

    @Test
    public void isEmpty() {
        String message = "不能为空";
        Verifier.init().isEmpty(null, message).validate();
        ArrayList<Object> list = new ArrayList<>();
        Verifier.init().isEmpty(list, message).validate();

        list.add(11);
        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isEmpty(list, message).validate();
        });
        Assertions.assertEquals(message, ex1.getMessage());
    }

    @Test
    public void isFalse() {
        String message = "必须为false";
        Verifier.init().isFalse(false, message).validate();

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isFalse(true, message).validate();
        });
        Assertions.assertEquals(message, ex1.getMessage());
    }

    @Test
    public void isTrue() {
        String message = "必须为true";
        Verifier.init().isTrue(true, message).validate();

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isTrue(false, message).validate();
        });
        Assertions.assertEquals(message, ex1.getMessage());
    }

    @Test
    public void isMinSize() {
        String message = "最小个数必须大于{0}";
        int minSize = 2;
        ArrayList<Object> list = new ArrayList<>();
        list.add(11);
        list.add(22);
        Verifier.init().isMinSize(list, minSize, message).validate();

        list.clear();
        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isMinSize(list, minSize, message).validate();
        });
        Assertions.assertEquals(MessageFormat.format(message, minSize), ex1.getMessage());
    }

    @Test
    public void isMaxSize() {
        String message = "最大个数必须小于等于{0}";
        int maxSize = 2;
        ArrayList<Object> list = new ArrayList<>();
        list.add(11);
        list.add(22);
        Verifier.init().isMaxSize(list, maxSize, message).validate();

        list.add(33);
        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isMaxSize(list, maxSize, message).validate();
        });
        Assertions.assertEquals(MessageFormat.format(message, maxSize), ex1.getMessage());
    }

    @Test
    public void isEqSize() {
        String message = "个数必须等于{0}";
        int size = 2;
        ArrayList<Object> list = new ArrayList<>();
        list.add(11);
        list.add(22);
        Verifier.init().isEqSize(list, size, message).validate();

        list.add(33);
        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isEqSize(list, size, message).validate();
        });
        Assertions.assertEquals(MessageFormat.format(message, size), ex1.getMessage());

        list.clear();
        Exception ex2 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isEqSize(list, size, message).validate();
        });
        Assertions.assertEquals(MessageFormat.format(message, size), ex2.getMessage());
    }

    @Test
    public void isEq() {
        String message = "值必须相等";
        Verifier.init().isEq("aa", "aa", message).validate();
        Verifier.init().isEq(12345, 12345, message).validate();

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isEq("aa", "bb", message).validate();
        });
        Assertions.assertEquals(message, ex1.getMessage());

        Exception ex2 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isEq(12345, 12346, message).validate();
        });
        Assertions.assertEquals(message, ex2.getMessage());
    }

    @Test
    public void isNotEq() {
        String message = "值必须不相等";
        Verifier.init().isNotEq("aa", "aa1", message).validate();

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isNotEq("aa", "aa", message).validate();
        });
        Assertions.assertEquals(message, ex1.getMessage());
    }

    @Test
    public void isEqIgnoreCase() {
        String message = "值必须相等,忽略大小写";
        Verifier.init().isEqIgnoreCase("aa", "aa", message).validate();
        Verifier.init().isEqIgnoreCase("aa", "AA", message).validate();

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isEqIgnoreCase("aa", "aa1", message).validate();
        });
        Assertions.assertEquals(message, ex1.getMessage());
    }

    @Test
    public void isNotEqIgnoreCase() {
        String message = "值必须不相等,忽略大小写";
        Verifier.init().isNotEqIgnoreCase("aa", "aaa", message).validate();

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isNotEqIgnoreCase("aa", "AA", message).validate();
        });
        Assertions.assertEquals(message, ex1.getMessage());
    }

    @Test
    public void isGtThan() {
        String message = "值必须大于{0}";
        Verifier.init().isGtThan(5, 4, message).validate();

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isGtThan(5, 5, message).validate();
        });
        Assertions.assertEquals(MessageFormat.format(message, 5), ex1.getMessage());

        Exception ex2 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isGtThan(5, 8, message).validate();
        });
        Assertions.assertEquals(MessageFormat.format(message, 8), ex2.getMessage());
    }

    @Test
    public void isGtEq() {
        String message = "值必须大于等于{0}";
        Verifier.init().isGtEq(5, 4, message).validate();
        Verifier.init().isGtEq(5, 5, message).validate();

        Exception ex2 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isGtEq(5, 8, message).validate();
        });
        Assertions.assertEquals(MessageFormat.format(message, 8), ex2.getMessage());
    }

    @Test
    public void isLtThan() {
        String message = "值必须小于{0}";
        Verifier.init().isLtThan(5, 6, message).validate();

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isLtThan(5, 5, message).validate();
        });
        Assertions.assertEquals(MessageFormat.format(message, 5), ex1.getMessage());

        Exception ex2 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isLtThan(5, 4, message).validate();
        });
        Assertions.assertEquals(MessageFormat.format(message, 4), ex2.getMessage());
    }

    @Test
    public void isLtEq() {
        String message = "值必须小于等于{0}";
        Verifier.init().isLtEq(5, 6, message).validate();
        Verifier.init().isLtEq(5, 5, message).validate();

        Exception ex2 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isLtEq(5, 4, message).validate();
        });
        Assertions.assertEquals(MessageFormat.format(message, 4), ex2.getMessage());
    }

    @Test
    public void isEachNotNull() {
        String message = "不能有null元素";

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isEachNotNull(null, message).validate();
        });
        Assertions.assertEquals(message, ex1.getMessage());

        ArrayList<Object> list = new ArrayList<>();
        list.add(111);
        Verifier.init().isEachNotNull(list, message).validate();

        list.add(null);
        Exception ex2 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isEachNotNull(list, message).validate();
        });
        Assertions.assertEquals(message, ex2.getMessage());
    }


    @Test
    public void isRegexMatch() {
        String message = "只能包含数字";
        String regex = "^\\d+$";
        Verifier.init().isRegexMatch("123456", regex, message).validate();

        Exception ex2 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isRegexMatch("a123456", regex, message).validate();
        });
        Assertions.assertEquals(message, ex2.getMessage());

    }


    @Test
    public void with() {
        String message = "值必须等于";
        Verifier.init().isEq("aa", "bb", message).with(false).validate();

        Exception ex2 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isEq("aa", "bb", message).with(true).validate();
        });
        Assertions.assertEquals(message, ex2.getMessage());
    }

    @Test
    public void errorCode() {
        String message = "值必须相等";
        String errorCode = "val.eq.result";
        ValidationException ex2 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isEq("aa", "bb", message).errorCode(errorCode).validate();
        });
        Assertions.assertEquals(errorCode, ex2.getResultCode());
    }

    @Test
    public void errorMessage() {
        String message = "值必须相等";
        String errorMsg = "两个值必须相等";
        ValidationException ex2 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.init().isEq("aa", "bb", message).errorMessage(errorMsg).validate();
        });
        Assertions.assertEquals(errorMsg, ex2.getResultMsg());
    }

    @Test
    public void result() {
        String message = "值必须相等";
        ValidationResult result = Verifier.init().isEq("aa", "bb", message).result();
        List<ValidationError> errors = result.getErrors();
        Assertions.assertEquals(1, errors.size());
        Assertions.assertEquals(message, errors.iterator().next().getErrorMsg());
    }

    @Test
    public void validate() {
        ValidationModel validationModel = new ValidationModel();
        List<String> validate = Verifier.validate(validationModel, false, ValidationGroup.defaults(), ValidationGroup.Update.class);
        Assertions.assertEquals(4, validate.size());

        validationModel.setPassword("123456");
        List<String> validate1 = Verifier.validate(validationModel, false);
        Assertions.assertEquals(2, validate1.size());

        Exception ex1 = Assertions.assertThrows(ValidationException.class, () -> {
            Verifier.validate(validationModel, ValidationGroup.Update.class);
        });
        Assertions.assertEquals("id不能为空", ex1.getMessage());
    }
}
