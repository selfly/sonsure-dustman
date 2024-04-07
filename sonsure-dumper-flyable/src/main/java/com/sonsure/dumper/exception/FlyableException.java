package com.sonsure.dumper.exception;

import com.sonsure.dumper.common.exception.SonsureException;

public class FlyableException extends SonsureException {

    public FlyableException(String message) {
        super(message);
    }

    public FlyableException(Throwable e) {
        super(e);
    }
}
