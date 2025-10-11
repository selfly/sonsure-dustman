package com.sonsure.dumper.common.model;

import lombok.Getter;

/**
 * @author selfly
 */
@Getter
public class MultiTuple<L, R> {

    private final L left;
    private final R right;

    public MultiTuple(L left, R right) {
        this.left = left;
        this.right = right;
    }
}
