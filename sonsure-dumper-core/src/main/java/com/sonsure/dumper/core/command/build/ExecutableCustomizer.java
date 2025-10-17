package com.sonsure.dumper.core.command.build;

/**
 * @author selfly
 */
public interface ExecutableCustomizer {

    default void customizeBuilder(ExecutableCmdBuilder executableCmdBuilder) {
    }

    default ExecutableCmd customizeCmd(ExecutableCmd executableCmd) {
        return executableCmd;
    }
}
