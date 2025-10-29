package com.sonsure.dumper.core.interceptor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author selfly
 */
public class InterceptorChain {

    private final List<PersistInterceptor> interceptors;

    private int currentPosition = 0;

    public InterceptorChain(List<PersistInterceptor> interceptors) {
        this.interceptors = Optional.ofNullable(interceptors)
                .orElse(Collections.emptyList());
    }

    public void addInterceptor(PersistInterceptor interceptor) {
        this.interceptors.add(interceptor);
    }

    public void reset() {
        this.currentPosition = 0;
    }

    public void doBefore(PersistContext context) {
        if (this.currentPosition == this.interceptors.size()) {
            return;
        }
        PersistInterceptor persistInterceptor = this.interceptors.get(this.currentPosition);
        this.currentPosition++;
        persistInterceptor.executeBefore(context);
    }

    public void doAfter(PersistContext context) {
        if (this.currentPosition == this.interceptors.size()) {
            return;
        }
        PersistInterceptor persistInterceptor = this.interceptors.get(this.currentPosition);
        this.currentPosition++;
        persistInterceptor.executeAfter(context);
    }
}
