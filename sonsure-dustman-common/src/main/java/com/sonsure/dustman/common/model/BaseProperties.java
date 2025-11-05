package com.sonsure.dustman.common.model;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author selfly
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface BaseProperties {
}
