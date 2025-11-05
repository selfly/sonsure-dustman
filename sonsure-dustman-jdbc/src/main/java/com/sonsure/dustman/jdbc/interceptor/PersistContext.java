package com.sonsure.dustman.jdbc.interceptor;

import com.sonsure.dustman.jdbc.command.build.ExecutableCmd;
import lombok.Getter;
import lombok.Setter;

/**
 * @author selfly
 */
@Getter
@Setter
public class PersistContext {

    private final ExecutableCmd executableCmd;
    private boolean skipExecution;
    private Object result;

    public PersistContext(ExecutableCmd executableCmd) {
        this.executableCmd = executableCmd;
    }
}
