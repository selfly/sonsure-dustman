package com.sonsure.dumper.common.validation;

import java.util.List;

/**
 * 适配 javax 或 jakarta
 *
 * @author selfly
 */
public interface ValidatorAdapter {

    <T> List<String> validate(T object, boolean throwsExp, Class<?>... groups);

}
