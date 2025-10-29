package com.sonsure.dumper.core.interceptor;

import com.sonsure.dumper.core.command.build.ExecutableCmd;
import lombok.Getter;
import lombok.Setter;

/**
 * @author selfly
 */
@Getter
@Setter
public class PersistContext {

    private final String dialect;
    private final ExecutableCmd executableCmd;
    private boolean skipExecution;
    private Object result;

    public PersistContext(String dialect, ExecutableCmd executableCmd) {
        this.dialect = dialect;
        this.executableCmd = executableCmd;
    }
}
