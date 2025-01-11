package com.sonsure.dumper.common.validation;

import javax.validation.groups.Default;

/**
 * @author selfly
 * <p>
 * The type Validation group.
 */
public class ValidationGroup {

    public static Class<?> defaults() {
        return Default.class;
    }

    public interface Add {
    }

    public interface Update {
    }

    public interface Delete {
    }

    public interface Query {
    }

    public interface Select {
    }

    public interface Insert {
    }
}
