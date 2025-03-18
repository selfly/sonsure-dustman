package com.sonsure.dumper.core.command;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author selfly
 */
@Getter
public class NativeContentWrapper {

    private final boolean natives;
    private final String actualContent;

    public NativeContentWrapper(String content) {
        if (CommandBuildHelper.isNativeContent(content)) {
            this.natives = true;
            this.actualContent = CommandBuildHelper.getNativeContentActualValue(content);
        } else {
            this.natives = false;
            this.actualContent = content;
        }
    }

}
