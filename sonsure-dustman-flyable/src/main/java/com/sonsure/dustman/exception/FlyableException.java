package com.sonsure.dustman.exception;

import com.sonsure.dustman.common.exception.SonsureException;

/**
 * @author selfly
 */
public class FlyableException extends SonsureException {

    public FlyableException(String message) {
        super(message);
    }

    public FlyableException(Throwable e) {
        super(e);
    }
}
