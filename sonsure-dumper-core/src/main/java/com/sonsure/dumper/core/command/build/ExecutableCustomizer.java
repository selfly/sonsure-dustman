package com.sonsure.dumper.core.command.build;

/**
 * @author selfly
 */
public interface ExecutableCustomizer {

    /**
     * Customize.
     *
     * @param executableCmdBuilder the executable cmd builder
     */
    void customize(ExecutableCmdBuilder executableCmdBuilder);
}
