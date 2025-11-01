package com.sonsure.dumper.core.interceptor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author selfly
 */
public class InterceptorChain {

    public static final int EXECUTION_BEFORE = 1;
    public static final int EXECUTION_AFTER = 2;

    private final List<PersistInterceptor> interceptors;

    private int currentPosition = 0;
    private int execution = EXECUTION_BEFORE;

    public InterceptorChain(List<PersistInterceptor> interceptors) {
        this.interceptors = Optional.ofNullable(interceptors).orElse(Collections.emptyList());
    }

    public void reset(int execution) {
        this.currentPosition = 0;
        this.execution = execution;
    }

    public void execute(PersistContext context) {
        if (this.currentPosition == this.interceptors.size()) {
            return;
        }
        PersistInterceptor persistInterceptor = this.interceptors.get(this.currentPosition);
        this.currentPosition++;
        if (this.execution == EXECUTION_BEFORE) {
            persistInterceptor.executeBefore(context, this);
        } else {
            persistInterceptor.executeAfter(context, this);
        }
    }
}
